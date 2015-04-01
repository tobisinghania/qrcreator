/*
 * Copyright 2009 ZXing authors
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
package qrcreator

import groovy.transform.CompileStatic

import java.awt.image.BufferedImage
import java.nio.file.Path

import javax.imageio.ImageIO

/**
 * Writes a {@link BitMatrix} to {@link BufferedImage},
 * file or stream. Provided here instead of core since it depends on
 * Java SE libraries.
 *
 * @author Sean Owen
 *
 * Modified for Groovy compatibility
 * @author Tobias Singhania
 */
@CompileStatic
class MatrixToImageWriter {

    private static final MatrixToImageConfig DEFAULT_CONFIG = new MatrixToImageConfig()

    private MatrixToImageWriter() {}

    /**
     * Renders a {@link BitMatrix} as an image, where "false" bits are rendered
     * as white, and "true" bits are rendered as black. Uses default configuration.
     *
     * @param matrix {@link BitMatrix} to write
     * @return {@link BufferedImage} representation of the input
     */
    static BufferedImage toBufferedImage(BitMatrix matrix) {
        return toBufferedImage(matrix, DEFAULT_CONFIG)
    }

    /**
     * As {@link #toBufferedImage(BitMatrix)}, but allows customization of the output.
     *
     * @param matrix {@link BitMatrix} to write
     * @param config output configuration
     * @return {@link BufferedImage} representation of the input
     */
    static BufferedImage toBufferedImage(BitMatrix matrix, MatrixToImageConfig config) {
        int width = matrix.width
        int height = matrix.height
        BufferedImage image = new BufferedImage(width, height, config.bufferedImageColorModel)
        int onColor = config.pixelOnColor
        int offColor = config.pixelOffColor
        width.times { int x ->
            height.times { int y ->
                image.setRGB(x, y, matrix.get(x, y) ? onColor : offColor)
            }
        }
        return image
    }

    /**
     * @param matrix {@link BitMatrix} to write
     * @param format image format
     * @param file file {@link File} to write image to
     * @throws IOException if writes to the file fail
     * @deprecated use {@link #writeToPath(BitMatrix, String, Path)}
     */
    @Deprecated
    static void writeToFile(BitMatrix matrix, String format, File file) throws IOException {
        writeToPath(matrix, format, file.toPath())
    }

    /**
     * Writes a {@link BitMatrix} to a file with default configuration.
     *
     * @param matrix {@link BitMatrix} to write
     * @param format image format
     * @param file file {@link Path} to write image to
     * @throws IOException if writes to the stream fail
     * @see #toBufferedImage(BitMatrix)
     */
    static void writeToPath(BitMatrix matrix, String format, Path file) throws IOException {
        writeToPath(matrix, format, file, DEFAULT_CONFIG)
    }

    /**
     * @param matrix {@link BitMatrix} to write
     * @param format image format
     * @param file file {@link File} to write image to
     * @param config output configuration
     * @throws IOException if writes to the file fail
     * @deprecated use {@link #writeToPath(BitMatrix, String, Path, MatrixToImageConfig)}
     */
    @Deprecated
    static void writeToFile(BitMatrix matrix, String format, File file, MatrixToImageConfig config)
            throws IOException {
        writeToPath(matrix, format, file.toPath(), config)
    }

    /**
     * As {@link #writeToFile(BitMatrix, String, File)}, but allows customization of the output.
     *
     * @param matrix {@link BitMatrix} to write
     * @param format image format
     * @param file file {@link Path} to write image to
     * @param config output configuration
     * @throws IOException if writes to the file fail
     */
    static void writeToPath(BitMatrix matrix, String format, Path file, MatrixToImageConfig config)
            throws IOException {
        BufferedImage image = toBufferedImage(matrix, config)
        if (!ImageIO.write(image, format, file.toFile())) {
            throw new IOException("Could not write an image of format $format to $file")
        }
    }

    /**
     * Writes a {@link BitMatrix} to a stream with default configuration.
     *
     * @param matrix {@link BitMatrix} to write
     * @param format image format
     * @param stream {@link OutputStream} to write image to
     * @throws IOException if writes to the stream fail
     * @see #toBufferedImage(BitMatrix)
     */
    static void writeToStream(BitMatrix matrix, String format, OutputStream stream) throws IOException {
        writeToStream(matrix, format, stream, DEFAULT_CONFIG)
    }

    /**
     * As {@link #writeToStream(BitMatrix, String, OutputStream)}, but allows customization of the output.
     *
     * @param matrix {@link BitMatrix} to write
     * @param format image format
     * @param stream {@link OutputStream} to write image to
     * @param config output configuration
     * @throws IOException if writes to the stream fail
     */
    static void writeToStream(BitMatrix matrix, String format, OutputStream stream, MatrixToImageConfig config)
            throws IOException {
        BufferedImage image = toBufferedImage(matrix, config)
        if (!ImageIO.write(image, format, stream)) {
            throw new IOException("Could not write an image of format $format")
        }
    }
}
