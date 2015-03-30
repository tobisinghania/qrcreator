package qrcreator

import org.codehaus.groovy.grails.validation.routines.UrlValidator

/*
 * Copyright 2015 Tobias Singhania
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


import javax.imageio.ImageIO
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Image
import java.awt.image.BufferedImage

/**
 * @author Tobias Singhania
 */

class LogoDesign extends Design {

    int logoWidthPct
    int logoHeightPct
    BufferedImage logo

    LogoDesign(String logo, int width, int height, int logoWidthPct, int logoHeightPct, int qz = 4)
            throws IOException
    {
        super(width, height, qz)
        BufferedImage img = null;
        def urlVal = new UrlValidator()
        if (urlVal.isValid(logo))
            this.logo = ImageIO.read(new URL(logo));
        else
            this.logo = ImageIO.read(new File("web-app/"+logo));

        this.logoHeightPct = logoHeightPct
        this.logoWidthPct = logoWidthPct
    }


    @Override
    BufferedImage getImage(QRCode val) {
        ByteMatrix input = val.getMatrix();
        if (input == null) throw new IllegalStateException();

        super.loadSizes(input.getWidth(), input.getHeight());

        BufferedImage out = new BufferedImage(width,height, BufferedImage.TYPE_INT_RGB)
        def g = out.createGraphics()
        g.setPaint(Color.white)
        g.fillRect(0,0,out.getWidth(), out.getHeight())
        g.dispose()

        def outY = topPadding
        for (inY in 0..(inputHeight-1)) {
            def outX = leftPadding
            for (inX in 0..(inputWidth-1)) {
                if (input.get(inX, inY) == 1)
                    drawRect(out.createGraphics(), Color.black, outX, outY, blockSize, blockSize)
                outX += blockSize
            }
            outY += blockSize
        }

        placeLogo(out, logo);

//        if(!ImageIO.write(frame, "gif", new File("/tmp/myfristqr1.gif"))) {
//            throw new IOException("Could not write an image of format " + format + " to " + file);
//        }
        return out
    }

    private static void drawRect(Graphics2D graph, Color c, int outX, int outY, int sizeX, int sizeY) {
        graph.setColor(Color.black);
        graph.fillRect(outX, outY, sizeX, sizeX);
        graph.dispose()
    }

    private void placeLogo(BufferedImage frame, BufferedImage logo) {
        def l = resize(logo, (outputWidth*logoWidthPct/100) as int , (outputHeight*logoHeightPct/100) as int)
        def g = frame.getGraphics()
        g.drawImage(l, ((frame.getWidth()-l.getWidth())/2) as int,
                ((frame.getHeight()-l.getHeight())/2) as int,null)
        g.dispose()
    }

    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }
}
