package org.contentmine.image.general;

import java.io.File;

import javax.imageio.ImageIO;

import org.junit.Test;
import org.contentmine.image.ImageAnalysisFixtures;
import org.contentmine.image.moments.ImageMomentGenerator;

public class ImageMomentGeneratorTest {

	@Test
	public void testImageMomentGenerator() throws Exception {
		ImageMomentGenerator imageMomentGenerator = new ImageMomentGenerator();
		imageMomentGenerator.readImage(ImageIO.read(new File(ImageAnalysisFixtures.REFFONT_DIR, "65.png")));
		imageMomentGenerator.readImage(ImageIO.read(new File(ImageAnalysisFixtures.REFFONT_DIR, "66.png")));
		imageMomentGenerator.readImage(ImageIO.read(new File(ImageAnalysisFixtures.REFFONT_DIR, "71.png")));
	}
}
