package org.contentmine.pdf2svg2;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGSVG;
import org.junit.Assert;
import org.junit.Test;

public class SVGCreatorTest {

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
	public void testCreator2() throws InvalidPasswordException, IOException {
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
	}

}
