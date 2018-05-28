package org.contentmine.graphics.svg.plot.forest;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.graphics.svg.plot.forest.ForestPlot;
import org.junit.Test;

import junit.framework.Assert;

public class ForestTest {
	public static final Logger LOG = Logger.getLogger(ForestTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	private static File inputDir = SVGHTMLFixtures.FOREST_DIR;
	private static File targetDir = new File("target/forest/");


	@Test
	public void testBlueRhombsGreenSquaresSVG() {
		String fileRoot = "blueRhombsGreenSquares";
		ForestPlot forestPlot = new ForestPlot();
		forestPlot.setFileRoot(fileRoot);
//		forestPlot.setOutputDir(new File(targetDir, "forest/"));
		forestPlot.readCacheAndAnalyze(inputDir, fileRoot);
		Assert.assertTrue("exists: "+forestPlot.getPolyListFile(), forestPlot.getPolyListFile().exists());
		Assert.assertEquals("rects", 12, forestPlot.getOrCreateRectList().size());
		Assert.assertEquals("horizontal", 15, forestPlot.getOrCreateHorizontalLineList().size());
		// some of these are glyphs
		Assert.assertEquals("vertical", 11, forestPlot.getOrCreateVerticalLineList().size());
		Assert.assertEquals("rhombs", 7, forestPlot.getOrCreateRhombList().size());
	}

	@Test
	public void testHollowRhombGraySquaresSVG() {
		String fileRoot = "hollowRhombsGraySquares";
		ForestPlot forestPlot = new ForestPlot();
		forestPlot.readCacheAndAnalyze(inputDir, fileRoot);
		Assert.assertEquals("rects", 24, forestPlot.getOrCreateRectList().size());
		Assert.assertEquals("horizontal", 14, forestPlot.getOrCreateHorizontalLineList().size());
		// ??? FIXME maybe 1?
		Assert.assertEquals("rhombs", 0, forestPlot.getOrCreateRhombList().size());
		// some are glyphs
		Assert.assertEquals("vertical", 5, forestPlot.getOrCreateVerticalLineList().size());

	}
	
	@Test
	public void testBitmap() {
		
	}

}
