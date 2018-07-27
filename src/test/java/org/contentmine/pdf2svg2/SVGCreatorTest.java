package org.contentmine.pdf2svg2;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGSVG;
import org.junit.Assert;
import org.junit.Test;

public class SVGCreatorTest {
	public static final Logger LOG = Logger.getLogger(SVGCreatorTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testCreator() throws InvalidPasswordException, IOException {
        File file = new File("src/test/resources/org/contentmine/pdf2svg2/",
                "custom-render-demo.pdf");
	    AMISVGCreator svgCreator = new AMISVGCreator();
	    SVGG svgg = svgCreator.createSVG(file);
	    File svgFile = new File("target/pdf2svg2/examples/custom.svg");
		SVGSVG.wrapAndWriteAsSVG(svgg, svgFile);
		BufferedImage image = svgCreator.createRenderedImageList().get(0);
		ImageIO.write(image, "png", new File("target/pdf2svg2/examples/custom.png"));
	    Assert.assertTrue("svg file exists", svgFile.exists());
	}

	@Test
	public void testCreator1() throws InvalidPasswordException, IOException {
        File file = new File("src/test/resources/org/contentmine/pdf2svg/", "page6.pdf");
	    AMISVGCreator svgCreator = new AMISVGCreator();
	    SVGG svgg = svgCreator.createSVG(file);
	    String fileroot = "target/pdf2svg2/examples/page6/";
		File svgFile = new File(fileroot, "page6.svg");
		SVGSVG.wrapAndWriteAsSVG(svgg, svgFile);
		BufferedImage image = svgCreator.createRenderedImageList().get(0);
		ImageIO.write(image, "png", new File(fileroot, "page6.png"));
	    Assert.assertTrue("svg file exists", svgFile.exists());
	}

	@Test
	public void testCreatorBMC() throws InvalidPasswordException, IOException {
        File file = new File("src/test/resources/org/contentmine/pdf2svg/bmc/", "1471-2148-11-329.pdf");
	    AMISVGCreator svgCreator = new AMISVGCreator();
	    SVGG svgg = svgCreator.createSVG(file);
	    String fileroot = "target/pdf2svg2/bmc/1471-2148-11-329/";
		File svgFile = new File(fileroot, "full.svg");
		SVGSVG.wrapAndWriteAsSVG(svgg, svgFile);
	    Assert.assertTrue("svg file exists", svgFile.exists());
		List<BufferedImage> imageList = svgCreator.createRenderedImageList();
		for (int i = 0; i < imageList.size(); i++) {
			ImageIO.write(imageList.get(i), "png", new File(fileroot, "page."+i+".png"));
		}
		int i = 0;
		for (SVGG svgPage : svgCreator.getSVGPageList()) {
			SVGSVG.wrapAndWriteAsSVG(svgPage, new File(fileroot, "page."+(i++)+".svg"));
		}
	}

//	@Test
//	public void testCreator3GVSUPapers() throws InvalidPasswordException, IOException {
//        File dir = new File("src/test/resources/closed/");
//        File targetDir = new File("target/gvsu/");
//	    for (String fileroot : new String[]{"Callahan2009", "Devereux1950", /*, "gilbert1980", "Yudkin1978"*/}) {
//		    File file = new File(dir, fileroot+".pdf");
//		    AMISVGCreator svgCreator = new AMISVGCreator();
//		    SVGG svgg = svgCreator.createSVG(file);
//			File parent = new File(targetDir, fileroot);
//			parent.mkdirs();
////			svgCreator.writePageImages(parent);
//			svgCreator.writeSVGPages(parent);
////			svgCreator.writeRawImages(parent);
//	    }
//	}


}
