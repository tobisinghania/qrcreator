package qrcreator

import org.codehaus.groovy.runtime.EncodingGroovyMethods

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

/*
 * Copyright 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/**
 * This object renders a QR Code as a BitMatrix 2D array of greyscale values.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 *
 * @author Tobias Singhania added functions base64 encoded and custom designs
 */
public  class ImageWriter {

    private static  int QUIET_ZONE_SIZE = 4;

    public BitMatrix encode(String contents, int width, int height)
            throws IOException {

        return encode(contents, width, height, null, null);
    }

    public BitMatrix encode(String contents, int width, int height, String ecl, int qz = QUIET_ZONE_SIZE)
            throws IOException {

        return encode(contents, width, height, null, ecl, qz);
    }

    public String encodeBase64(String contents, String ecl, Design design ) {
        BufferedImage img = encode(contents, ecl, design)

        def os = new ByteArrayOutputStream()
        ImageIO.write(img, 'png', os)
        return EncodingGroovyMethods.encodeBase64(os.toByteArray()).toString()
    }

    public String encodeBase64(String contents, String ecl, int width, int height, int qz = QUIET_ZONE_SIZE) {
        def img = encode(contents, width, height, ecl, qz)
        def os = new ByteArrayOutputStream()
        MatrixToImageWriter.writeToStream(img, 'png', os)
        return EncodingGroovyMethods.encodeBase64(os.toByteArray())
    }


    public BufferedImage encode(String contents, String ecl, Design design ) {
        def hints
        if (contents.isEmpty()) {
            throw new IllegalArgumentException("Found empty contents");
        }

        if (design.getWidth() < 0 || design.getHeight() < 0) {
            throw new IllegalArgumentException("Requested dimensions are too small" );
        }

        def errorCorrectionLevel = ErrorCorrectionLevel.L
        try {
            if (ecl != null)
                errorCorrectionLevel = ecl as ErrorCorrectionLevel
        } catch (IllegalArgumentException e) {}
        if (hints != null) {
            ErrorCorrectionLevel requestedECLevel = (ErrorCorrectionLevel) hints.get(EncodeHintType.ERROR_CORRECTION);
            if (requestedECLevel != null) {
                errorCorrectionLevel = requestedECLevel;
            }
        }

        QRCode code = Encoder.encode(contents, errorCorrectionLevel, hints);
        return design.getImage(code)
    }

    public BitMatrix encode(String contents,
                            int width,
                            int height,
                            Map<EncodeHintType,?> hints, String ecl, int qz = QUIET_ZONE_SIZE) throws IOException {

        if (contents.isEmpty()) {
            throw new IllegalArgumentException("Found empty contents");
        }

        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Requested dimensions are too small: " + width + 'x' +
                    height);
        }

        def errorCorrectionLevel = ErrorCorrectionLevel.L
        try {
            if (ecl != null)
                errorCorrectionLevel = ecl as ErrorCorrectionLevel
        } catch (IllegalArgumentException e) {}
        int quietZone = qz
        if (hints != null) {
            ErrorCorrectionLevel requestedECLevel = (ErrorCorrectionLevel) hints.get(EncodeHintType.ERROR_CORRECTION);
            if (requestedECLevel != null) {
                errorCorrectionLevel = requestedECLevel;
            }
            Integer quietZoneInt = (Integer) hints.get(EncodeHintType.MARGIN);
            if (quietZoneInt != null) {
                quietZone = quietZoneInt;
            }
        }

        QRCode code = Encoder.encode(contents, errorCorrectionLevel, hints);
        return renderResult(code, width, height, quietZone);
    }

    // Note that the input matrix uses 0 == white, 1 == black, while the output matrix uses
    // 0 == black, 255 == white (i.e. an 8 bit greyscale bitmap).
    private static BitMatrix renderResult(QRCode code, int width, int height, int quietZone) {
        ByteMatrix input = code.getMatrix();
        if (input == null) {
            throw new IllegalStateException();
        }
        int inputWidth = input.getWidth();
        int inputHeight = input.getHeight();
        int qrWidth = inputWidth + (quietZone * 2);
        int qrHeight = inputHeight + (quietZone * 2);
        int outputWidth = Math.max(width, qrWidth);
        int outputHeight = Math.max(height, qrHeight);

        int multiple = Math.min(outputWidth / qrWidth, outputHeight / qrHeight);
        // Padding includes both the quiet zone and the extra white pixels to accommodate the requested
        // dimensions. For example, if input is 25x25 the QR will be 33x33 including the quiet zone.
        // If the requested size is 200x160, the multiple will be 4, for a QR of 132x132. These will
        // handle all the padding from 100x100 (the actual QR) up to 200x160.
        int leftPadding = (outputWidth - (inputWidth * multiple)) / 2;
        int topPadding = (outputHeight - (inputHeight * multiple)) / 2;

        BitMatrix output = new BitMatrix(outputWidth, outputHeight);

        def outputY = topPadding
        for (int inputY = 0; inputY < inputHeight; inputY++) {
            // Write the contents of this row of the barcode
            def outputX = leftPadding
            for (int inputX = 0; inputX < inputWidth; inputX++) {
                if (input.get(inputX, inputY) == 1) {
                    output.setRegion(outputX, outputY, multiple, multiple);
                }
                outputX += multiple
            }
            outputY += multiple
        }

        return output;
    }

}
