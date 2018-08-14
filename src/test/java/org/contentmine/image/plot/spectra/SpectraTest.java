package org.contentmine.image.plot.spectra;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.image.diagram.DiagramAnalyzer;
import org.contentmine.image.pixel.PixelIslandList;
import org.junit.Test;

public class SpectraTest {
	private static final Logger LOG = Logger.getLogger(SpectraTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void readSpectrum() {
		String base = "page.110.image.0";
		File sourceDir = new File(SVGHTMLFixtures.I_SPECTRA_DIR, "paper1");
		File targetDir = new File(SVGHTMLFixtures.I_SPECTRA_TARGET_DIR, base);
		DiagramAnalyzer diagramAnalyzer = new DiagramAnalyzer()
			.setBase(base)
			.setInputDir(sourceDir)
			.setOutputDir(targetDir)
			.setThinning(null)
//			.setThreshold(135)
			;
		LOG.debug("created DA");
		diagramAnalyzer.readAndProcessInputFile();
		diagramAnalyzer.writeBinarizedFile(new File(targetDir, "binarized0.png"));
		LOG.debug("read DA");
//		LOG.debug(diagramAnalyzer.getOrCreateSortedPixelIslandList());
		diagramAnalyzer.writeBinarizedFile(new File(targetDir, "binarized.png"));
		LOG.debug("wrote binarized");
//		PixelIslandList pixelIslandList = diagramAnalyzer.writeLargestPixelIsland();

//		diagramAnalyzer.readAndProcessInputFile();
		int[] thresholds = new int[]{50, 70, 90, 110, 130, 150, 170, 190, 210};
		diagramAnalyzer.scanThresholds(thresholds);
//		diagramAnalyzer.writeBinarizedFile(new File(targetDir, "binarized.png"));
		PixelIslandList pixelIslandList = diagramAnalyzer.writeLargestPixelIsland();

	}
}
