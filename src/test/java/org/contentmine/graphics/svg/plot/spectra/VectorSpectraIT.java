package org.contentmine.graphics.svg.plot.spectra;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.eucl.euclid.test.TestUtil;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.pdf2svg2.AMISVGCreator;
import org.junit.Ignore;
import org.junit.Test;

import junit.framework.Assert;

public class VectorSpectraIT {
	private static final Logger LOG = Logger.getLogger(VectorSpectraIT.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}


	@Test
	public void testCopyPDF2Target() {
		String fileroot = "rsc";
		File sourceDir = new File(SVGHTMLFixtures.G_SPECTRA_PLOT_DIR, fileroot);
		Assert.assertTrue("spectra "+SVGHTMLFixtures.G_SPECTRA_PLOT_DIR, SVGHTMLFixtures.G_SPECTRA_PLOT_DIR.exists());
		Assert.assertTrue("rsc "+sourceDir, sourceDir.exists());
		File targetDir = new File(SVGHTMLFixtures.G_SPECTRA_PLOT_TARGET_DIR, fileroot);
		CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
		Assert.assertTrue("target "+targetDir, targetDir.exists());
		
	}

	@Test
	public void testMakeProject() {
		String fileroot = "rsc";
		File sourceDir = new File(SVGHTMLFixtures.G_SPECTRA_PLOT_DIR, fileroot);
		File targetDir = new File(SVGHTMLFixtures.G_SPECTRA_PLOT_TARGET_DIR, fileroot);
		CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
		CProject cProject = new CProject();
		String command = "--project " + targetDir + CProject.MAKE_PROJECT;
		cProject.run(command);
		Assert.assertTrue("target pdf "+targetDir, new File(targetDir, "c8ob00931g1").exists());
		
	}

	@Test
	public void testPDF2SVG() throws Exception {
		String fileroot = "rsc";
		File sourceDir = new File(SVGHTMLFixtures.G_SPECTRA_PLOT_DIR, fileroot);
		File targetDir = new File(SVGHTMLFixtures.G_SPECTRA_PLOT_TARGET_DIR, fileroot);
		CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
		CProject cProject = new CProject();
		String command = "--project " + targetDir + CProject.MAKE_PROJECT;
		cProject.run(command);
//		String ctreeS = "c8ob00931g1";
//		String ctreeS = "c8ob00998h1";
		String ctreeS = "c8ob00847g1";
		File ctreeFile = new File(targetDir, ctreeS);
		Assert.assertTrue("target pdf "+targetDir, ctreeFile.exists());
        File file = new File(ctreeFile, "fulltext.pdf");
	    AMISVGCreator svgCreator = new AMISVGCreator();
	    SVGG svgg = svgCreator.createSVG(file);
	    svgCreator.writeSVGPages(targetDir);
	    svgCreator.writeRawImages(targetDir);
	}

	@Test
	@Ignore // too long
	public void testPDF2SVG3Files() throws Exception {
		String fileroot = "rsc";
		File sourceDir = new File(SVGHTMLFixtures.G_SPECTRA_PLOT_DIR, fileroot);
		File targetDir = new File(SVGHTMLFixtures.G_SPECTRA_PLOT_TARGET_DIR, fileroot);
		CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
		CProject cProject = new CProject();
		String command = "--project " + targetDir + CProject.MAKE_PROJECT;
		cProject.run(command);
		String[] ctreeNames = {
			"c8ob00931g1",
			"c8ob00998h1",
			"c8ob00847g1",
		};
		for (int i = 0; i < ctreeNames.length; i++) {
			String ctree = ctreeNames[i];
			writeSVGAndPNG(targetDir, ctree);
		}
	}

	@Test
	@Ignore // too long
	public void testPDF2SVG50RSCArticles() throws Exception {
		String fileroot = "journals2";
//		File sourceDir = new File(SVGHTMLFixtures.SPECTRA_PLOT_DIR, fileroot);
		File sourceDir = new File("/Users/pm286/workspace/projects/stefan/", fileroot);
		if (TestUtil.checkForeignDirExists(sourceDir)) {
			LOG.debug(sourceDir+" does not exist");
			return;
		}
		File targetDir = new File("target/projects/stefan", fileroot);
		CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
		CProject cProject = new CProject();
		String command = "--project " + targetDir + CProject.MAKE_PROJECT;
		cProject.run(command);
		File[] ctreeNames = targetDir.listFiles();
		List<File> cTreeFiles = Arrays.asList(ctreeNames);
		Collections.sort(cTreeFiles);
		for (File cTreeFile : cTreeFiles) {
			String ctree = cTreeFile.getName();
			writeSVGAndPNG(targetDir, ctree);
		}
	}

	private void writeSVGAndPNG(File targetDir, String ctree) throws IOException {
		File ctreeDir = new File(targetDir, ctree);
		Assert.assertTrue("target pdf "+targetDir, ctreeDir.exists());
		File file = new File(ctreeDir, "fulltext.pdf");
		AMISVGCreator svgCreator = new AMISVGCreator();
		svgCreator.createSVG(file);
		svgCreator.writeSVGPages(ctreeDir);
		svgCreator.writeRawImages(ctreeDir);
	}


}
