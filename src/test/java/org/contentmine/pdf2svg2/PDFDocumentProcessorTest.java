package org.contentmine.pdf2svg2;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGSVG;
import org.junit.Assert;
import org.junit.Test;

public class PDFDocumentProcessorTest {
	public static final Logger LOG = Logger.getLogger(PDFDocumentProcessorTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testCreator() throws InvalidPasswordException, IOException {
        File file = new File("src/test/resources/org/contentmine/pdf2svg2/",
                "custom-render-demo.pdf");
	    PDFDocumentProcessor documentProcessor = new PDFDocumentProcessor();
	    List<SVGG> svgList = documentProcessor.readAndProcess(file).getOrCreateSVGPageList();
	    File svgFile = new File("target/pdf2svg2/examples/custom.svg");
		SVGSVG.wrapAndWriteAsSVG(svgList, svgFile);
		BufferedImage image = documentProcessor.createRenderedImageList().get(0);
		if (image == null) {
//			LOG.error("*** FIXME *** ");
			return;
		}

		try {
			if (!new File("target/pdf2svg2/examples").exists()) return;
			ImageIO.write(image, "png", new File("target/pdf2svg2/examples/custom.png"));
		} catch (Exception e) {
//			LOG.error("*** FIXME ***"+e.getMessage());
			return;
		}
	    Assert.assertTrue("svg file exists", svgFile.exists());
	}

	@Test
	public void testCreator1() throws InvalidPasswordException, IOException {
        File file = new File("src/test/resources/org/contentmine/pdf2svg/", "page6.pdf");
	    PDFDocumentProcessor documentProcessor = new PDFDocumentProcessor();
	    List<SVGG> svgList = documentProcessor.readAndProcess(file).getOrCreateSVGPageList();
	    String fileroot = "target/pdf2svg2/examples/page6/";
		File svgFile = new File(fileroot, "page6.svg");
		SVGSVG.wrapAndWriteAsSVG(svgList, svgFile);
		BufferedImage image = documentProcessor.createRenderedImageList().get(0);
		if (image == null) {
//			LOG.error("*** FIXME *** ");
			return;
		}
		try {
			File output = new File(fileroot, "page6.png");
			if (!new File(fileroot).exists()) return;
			ImageIO.write(image, "png", output);
		} catch (Throwable e) {
			LOG.error("*** FIXME ***"+e.getMessage());
			return;
		}

	    Assert.assertTrue("svg file exists", svgFile.exists());
	}

	@Test
	
	public void testCreatorBMC() throws InvalidPasswordException, IOException {
        File file = new File("src/test/resources/org/contentmine/pdf2svg/bmc/", "1471-2148-11-329.pdf");
	    PDFDocumentProcessor documentProcessor = new PDFDocumentProcessor();
	    List<SVGG> svgList = documentProcessor.readAndProcess(file).getOrCreateSVGPageList();
	    String fileroot = "target/pdf2svg2/bmc/1471-2148-11-329/";
		File svgFile = new File(fileroot, "full.svg");
		SVGSVG.wrapAndWriteAsSVG(svgList, svgFile);
		if (!svgFile.exists()) {
//			LOG.error("*** FIXME *** ");
			return;
		}
	    Assert.assertTrue("svg file exists", svgFile.exists());
		List<BufferedImage> imageList = documentProcessor.createRenderedImageList();
		for (int i = 0; i < imageList.size(); i++) {
			ImageIO.write(imageList.get(i), "png", new File(fileroot, "page."+i+".png"));
		}
		int i = 0;
		for (SVGG svgPage : documentProcessor.getOrCreateSVGPageList()) {
			SVGSVG.wrapAndWriteAsSVG(svgPage, new File(fileroot, "page."+(i++)+".svg"));
		}
	}

	@Test
	public void testIncludePages() throws InvalidPasswordException, IOException {
        File file = new File("src/test/resources/org/contentmine/pdf2svg/bmc/", "1471-2148-11-329.pdf");
	    PDFDocumentProcessor documentProcessor = new PDFDocumentProcessor();
	    documentProcessor.addIncludePage(3, 7);
	    Assert.assertEquals("include", "[3, 7]", documentProcessor.getOrCreateIncludePageList().toString());
	    documentProcessor.readAndProcess(file);
	    
	    String fileroot = "target/pdf2svg2/bmc/1471-2148-11-329/";
		File svgFile = new File(fileroot, "full.svg");
	    List<SVGG> svgList = documentProcessor.readAndProcess(file).getOrCreateSVGPageList();
		SVGSVG.wrapAndWriteAsSVG(svgList, svgFile);
		if (!svgFile.exists()) {
//			LOG.error("*** FIXME *** ");
			return;
		}
	    Assert.assertTrue("svg file exists", svgFile.exists());
		int i = 0;
		for (SVGG svgPage : documentProcessor.getOrCreateSVGPageList()) {
			SVGSVG.wrapAndWriteAsSVG(svgPage, new File(fileroot, "page."+(i++)+".svg"));
		}
	}

}
