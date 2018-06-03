package org.contentmine.image.plot.early.map;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.image.ImageAnalysisFixtures;
import org.contentmine.image.diagram.DiagramAnalyzer;
import org.contentmine.image.pixel.PixelIsland;
import org.contentmine.image.pixel.PixelIslandList;
import org.contentmine.image.pixel.PixelList;
import org.contentmine.image.pixel.PixelListFloodFill;
import org.contentmine.image.pixel.PixelRingList;
import org.eclipse.jetty.util.log.Log;
import org.junit.Test;

import junit.framework.Assert;

/**
 * 
 * @author pm286
 *
 */
/**
 * extraction of semantics from Broad Street Cholera Map
 *
 */
public class CholeraTest {
	private static final Logger LOG = Logger.getLogger(CholeraTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static String[] FILL = new String[] { "orange", "green", "blue", "red", "cyan" };

	@Test
	public void testSnow() {
		String fileRoot = "Snow-cholera-map-1";
		File snowFile = new File(SVGHTMLFixtures.EARLY_MAP_DIR, fileRoot + ".jpg");
		DiagramAnalyzer diagramAnalyzer = new DiagramAnalyzer();
		diagramAnalyzer.readAndProcessInputFile(snowFile);
	}

	@Test
	public void testCholeraSmallExtractBlackIslands() {
		String fileRoot = "choleraSmall";
		File targetDir = new File(new File("target"), fileRoot);
		File snowFile = new File(SVGHTMLFixtures.EARLY_MAP_DIR, fileRoot + ".png");
		DiagramAnalyzer diagramAnalyzer = new DiagramAnalyzer();
		// get the blocks
		diagramAnalyzer.setThinning(null);
		diagramAnalyzer.readAndProcessInputFile(snowFile);
		PixelIslandList pixelIslandList = diagramAnalyzer.getOrCreatePixelIslandList();
		Assert.assertEquals("islands", 66, pixelIslandList.size());
		pixelIslandList.sortBySizeDescending();
		// get the largest one
		PixelIsland island0 = pixelIslandList.get(0);
		Assert.assertEquals("island0", 26600, island0.size());
		SVGG g = island0.getOrCreateSVGG();
		SVGSVG.wrapAndWriteAsSVG(g, new File(targetDir, "island0.svg"));
		// create the pixel rings in this island
		PixelRingList pixelRingList = island0.getOrCreateInternalPixelRings();
		Assert.assertEquals("pixelRingList", 22, pixelRingList.size());
		for (int i = 0; i < pixelRingList.size(); i++) {
			pixelRingList.get(i).plotPixels(g, FILL[i % FILL.length]);
		}
		SVGSVG.wrapAndWriteAsSVG(g, new File(targetDir, "pixelRings"+".svg"));
		// cut island at level 2 to create new sub-islands
		// disconnects the weakly connected islands but some are still merged
		PixelList list2 = pixelRingList.get(2);
		SVGG gg = list2.getOrCreateSVG();
		SVGSVG.wrapAndWriteAsSVG(gg, new File(targetDir, "pixelRings2"+".svg"));
		// severer cut
		PixelList list4 = pixelRingList.get(4);
		gg = list4.getOrCreateSVG();
		SVGSVG.wrapAndWriteAsSVG(gg, new File(targetDir, "pixelRings4"+".svg"));
		PixelListFloodFill pixelListFloodFill = new PixelListFloodFill(list4);
		PixelIslandList separatedIslandList = pixelListFloodFill.getIslandList();
		separatedIslandList.sortBySizeDescending();
		Assert.assertEquals("separated pixelRingList", 11, separatedIslandList.size());
		SVGSVG.wrapAndWriteAsSVG(separatedIslandList.getOrCreateSVGG(), new File(targetDir, "separatedIslands"+".svg"));
		// now build the outer pixel rings without the bridges, expand out into white space
		PixelIsland newIsland0 = separatedIslandList.get(0);
		SVGSVG.wrapAndWriteAsSVG(newIsland0.createSVG(), new File(targetDir, "newIsland0"+".svg"));
		PixelRingList shell1 = newIsland0.getNeighbouringShells(1);
		SVGG shell1SVG = shell1.getRing(0).getOrCreateSVG();
		LOG.debug(shell1SVG.toXML());
		SVGSVG.wrapAndWriteAsSVG(shell1SVG, new File(targetDir, "shell1"+".svg"));
		LOG.debug("fin");

	}

}
