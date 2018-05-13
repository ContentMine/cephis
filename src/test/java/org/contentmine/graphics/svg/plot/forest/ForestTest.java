package org.contentmine.graphics.svg.plot.forest;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.graphics.svg.plot.forest.ForestPlot;
import org.junit.Test;

public class ForestTest {
	public static final Logger LOG = Logger.getLogger(ForestTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	private static File inputDir = SVGHTMLFixtures.FOREST_DIR;


	@Test
	public void testForest1() {
		String fileRoot = "plot1";
		ForestPlot forestPlot = new ForestPlot();
		forestPlot.readCacheAndAnalyze(inputDir, fileRoot);
	}

	@Test
	public void testForest2() {
		String fileRoot = "plot2";
		ForestPlot forestPlot = new ForestPlot();
		forestPlot.readCacheAndAnalyze(inputDir, fileRoot);
	}

}
