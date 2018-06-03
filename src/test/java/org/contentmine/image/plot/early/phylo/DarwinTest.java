package org.contentmine.image.plot.early.phylo;

import java.io.File;

import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.image.ImageAnalysisFixtures;
import org.contentmine.image.diagram.DiagramAnalyzer;
import org.contentmine.image.pixel.PixelEdgeList;
import org.contentmine.image.pixel.PixelGraph;
import org.contentmine.image.pixel.PixelIsland;
import org.contentmine.image.pixel.PixelIslandList;
import org.contentmine.image.pixel.PixelNodeList;
import org.junit.Test;

import junit.framework.Assert;

public class DarwinTest {
	@Test
	public void testDarwin() {
		String fileRoot = "darwin";
		File darwinFile = new File(SVGHTMLFixtures.EARLY_PHYLO_DIR, fileRoot + ".jpg");
		DiagramAnalyzer diagramAnalyzer = new DiagramAnalyzer();
		diagramAnalyzer.readAndProcessInputFile(darwinFile);
		PixelIslandList pixelIslandList = diagramAnalyzer.getOrCreatePixelIslandList();
		pixelIslandList.sortBySizeDescending();
		Assert.assertEquals("darwin size", 16, pixelIslandList.size());
		PixelIsland pixelIsland0 = pixelIslandList.get(0);
		Assert.assertEquals("darwin main tree size", 2352, pixelIsland0.size());
		PixelGraph pixelGraph = new PixelGraph(pixelIsland0);
		PixelNodeList nodeList = pixelGraph.getOrCreateNodeList();
		Assert.assertEquals("nodes", 68,  nodeList.size());
		PixelEdgeList edgeList = pixelGraph.getOrCreateEdgeList();
		Assert.assertEquals("edges",  68, edgeList.size());
		SVGG g = new SVGG();
		pixelGraph.createAndDrawGraph(g);
		File targetFile = new File("target", fileRoot);
		SVGSVG.wrapAndWriteAsSVG(g, new File(targetFile, "graph.svg"));
		
		diagramAnalyzer.writeBinarizedFile(new File("target/" + fileRoot +"/binarized"+".png"));
	}

}
