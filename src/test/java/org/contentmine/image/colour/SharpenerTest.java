package org.contentmine.image.colour;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.contentmine.eucl.euclid.RealMatrix;
import org.contentmine.graphics.svg.util.ImageIOUtil;
import org.contentmine.image.ImageAnalysisFixtures;
import org.junit.Test;

import junit.framework.Assert;

/** sharpens an RGB Image
 * RGBMatrix routines fail so ths is an alternative
 * 
 * @author pm286
 *
 */
public class SharpenerTest {

	/** apply simple sharpening function to image.
	 * @throws IOException 
	 * 
	 * 
	 */
	@Test
	public void testSharpenImage() throws IOException {
		BufferedImage newImage = null;
		File colourFile = new File(ImageAnalysisFixtures.COMPOUND_DIR, "journal.pone.0095816.g002.png");
		BufferedImage  image = ImageIO.read(colourFile);
		if (image == null) {
			throw new RuntimeException("Cannot find / read file:"+colourFile);
		}
		Assert.assertEquals(13, image.getType());
		ImageIOUtil.writeImageQuietly(newImage, new File("target/sharpen/sharpened.png"));
	}

	public RealMatrix makeSharpenerMatrix() {
		int ncols = 3;
		int nrows = 3;
		// roughly gaussian shape
		double[] matrix = new double[] {
			-1.2, 0.36, -1.2,
			0.36, 3.36, 0.36,
			-1.2, 0.36, -1.2,
		};
		RealMatrix sharpenerMatrix = new RealMatrix(ncols, nrows, matrix);
		return sharpenerMatrix;
	}


}
