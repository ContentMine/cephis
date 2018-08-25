package org.contentmine.pdf2svg2;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.junit.Test;

import junit.framework.Assert;

public class BiorxivIT {
	private static final Logger LOG = Logger.getLogger(BiorxivIT.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testBiorxivMarchantiaAll() throws Exception {
		File sourceDir = SVGHTMLFixtures.BIORXIV_DIR;
		File targetDir = SVGHTMLFixtures.BIORXIV_TARGET_DIR;
		CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
		String[] files = {
		"103861",
		"188912",
		"199992",
//		"220731", // this one is long and complex
		"247593",
		"277350",
		"329839",
		"362400",
		"363937",
		"385682"
	};
		for (String fileroot : files) {
			File file = new File(targetDir, fileroot+".full.pdf");
			LOG.debug("******* "+file+" **********");
			Assert.assertTrue("target pdf "+targetDir, file.exists());
		    PDFDocumentProcessor documentProcessor = new PDFDocumentProcessor();
		    documentProcessor.setMinimumImageBox(100, 100);
		    documentProcessor.readAndProcess(file);
		    File outputDir = new File(targetDir, fileroot);
			documentProcessor.writeSVGPages(outputDir);
		    try {
		    	documentProcessor.writeRawImages(outputDir);
		    } catch (Exception e) {
		    	LOG.error("image creation NYI" + e);
		    }
		}
	}
}
