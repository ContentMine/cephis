package org.contentmine.image;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Int2Range;
import org.contentmine.eucl.euclid.IntMatrix;
import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.graphics.svg.util.ImageIOUtil;
import org.junit.Assert;
import org.junit.Test;

public class ImageUtilTest {
	private static final Logger LOG = Logger.getLogger(ImageUtilTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	/** clip rectangle out of image.
	 * 
	 * @throws IOException
	 */
	public void testClipSubImage() throws IOException {
		BufferedImage image = ImageIO.read(ImageAnalysisFixtures.MALTORYZINE_THINNED_PNG);
		Rectangle rect = new Rectangle(20, 50, 60, 85); // x0, y0, w, h
		Raster raster = image.getData(rect);
		Assert.assertEquals(60, raster.getWidth());
		Assert.assertEquals(85, raster.getHeight());
		BufferedImage subImage = new BufferedImage(raster.getWidth(), raster.getHeight(), image.getType());
		subImage.setData(raster);
		ImageIOUtil.writeImageQuietly(subImage, "target/subimage/subImage.png");
	}
	
	@Test
	/** clip a rectangle out of an image.
	 * 
	 * @throws IOException
	 */
	public void testClipSub() throws IOException {
		BufferedImage image = ImageIO.read(ImageAnalysisFixtures.MALTORYZINE_THINNED_PNG);
		Int2Range boundingBox = new Int2Range(new IntRange(20, 80), new IntRange(50, 135));
		BufferedImage subImage = ImageUtil.clipSubImage(image, boundingBox);
		ImageIOUtil.writeImageQuietly(subImage, "target/subimage/subImage1.png");
	}

	@Test
	/** shifts image by dx dy.
	 * 
	 */
	public void testReadGrayImage() throws IOException {
		BufferedImage image = ImageIO.read(new File(ImageAnalysisFixtures.CHAR_DIR, "65.png"));
		IntMatrix matrix = ImageUtil.getGrayMatrix(image);
//		System.out.println(matrix);
	}

	@Test
	/** shifts image by dx dy.
	 * 
	 */
	public void testShiftGrayImage() throws IOException {
		BufferedImage image = ImageIO.read(new File(ImageAnalysisFixtures.CHAR_DIR, "65.png"));
		BufferedImage shiftedImage = ImageUtil.shiftImage(image, 0.1, 0.2);
		ImageIOUtil.writeImageQuietly(shiftedImage, "target/shiftscale/shiftedImage.png");
	}

	@Test
	/** scales image.
	 * 
	 */
	public void testScaleAndInterpolate() throws IOException {
		BufferedImage image = ImageIO.read(new File(ImageAnalysisFixtures.CHAR_DIR, "65.png"));
		BufferedImage shiftedImage = ImageUtil.scaleAndInterpolate(image, 17, 13);
		ImageIOUtil.writeImageQuietly(shiftedImage, "target/shiftscale/scaledImage.png");
	}
	
	@Test
	/** invert colors
	 * 
	 */
	public void testInvertRGB() {
		int blackRgb = ImageUtil.setRgb(0, 0, 0);
		int blackFlip = ImageUtil.invertRgb(blackRgb);
		Assert.assertEquals(0x00ffffff, blackFlip);
		int whiteRgb = ImageUtil.setRgb(255, 255, 255);
		int whiteFlip = ImageUtil.invertRgb(whiteRgb);
		Assert.assertEquals(0x00000000, whiteFlip);
		int redRgb = ImageUtil.setRgb(255, 0, 0);
		int redFlip = ImageUtil.invertRgb(redRgb);
		Assert.assertEquals(0x0000ffff, redFlip);
	}

}
