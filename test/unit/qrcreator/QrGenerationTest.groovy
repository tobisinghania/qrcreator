package qrcreator

import org.junit.Test

import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.nio.file.FileSystems
/**
 * Created by tobi on 29.03.15.
 */
class QrGenerationTest {

    @Test
    def void testqrCodeGeneratorText() {
        def writer = new ImageWriter()
        def bitmatrix = writer.encode("This is a test string, which tells a story" +
                " without any meaning or point, therefore leaving the reader " +
                " as uninformed as he has been", 800, 800, "H");
        MatrixToImageWriter.writeToPath(bitmatrix, "png",
                FileSystems.getDefault().getPath("/tmp/", "qrcode.png"))
        def file = new File('/tmp/qrcode.png')
        assert file.exists()
//        file.delete()
    }

    @Test
    def void testqrCodeGeneratorTextDesign() {
        def writer = new ImageWriter()
        def img = writer.encode("My test string@2349087234987)+",
                "H", new LogoDesign("logo.png", 800,800, 21,21));
        assert img.getHeight()==800
        assert img.getWidth() ==800
    }

}
