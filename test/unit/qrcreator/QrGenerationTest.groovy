package qrcreator

import java.nio.file.FileSystems

import org.junit.Test

/**
 * @author tobi
 */
class QrGenerationTest {

    private writer = new ImageWriter()

    @Test
    void testqrCodeGeneratorText() {
        def bitmatrix = writer.encode("This is a test string, which tells a story" +
            " without any meaning or point, therefore leaving the reader " +
            " as uninformed as he has been", 800, 800, "H")
        MatrixToImageWriter.writeToPath(bitmatrix, "png",
            FileSystems.getDefault().getPath(".", "qrcode.png"))
        def file = new File('qrcode.png')
        assert file.exists()
//        file.delete()
    }

    @Test
    void testqrCodeGeneratorTextDesign() {
        def img = writer.encode("My test string@2349087234987)+",
            "H", new LogoDesign("images/logo.png", 800,800, 21,21))
        assert img.height == 800
        assert img.width == 800
    }
}
