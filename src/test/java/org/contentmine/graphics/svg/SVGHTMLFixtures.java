package org.contentmine.graphics.svg;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class SVGHTMLFixtures {
	

	private static final Logger LOG = Logger.getLogger(SVGHTMLFixtures.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}


	public static final File RESOURCES_DIR = new File("src/test/resources/");
	
	private static final String ORG_CONTENTMINE_GRAPHICS = "org/contentmine/graphics/";
	public static final File SVG_DIR = new File(RESOURCES_DIR, ORG_CONTENTMINE_GRAPHICS + "svg");
	public static final File MATHML_DIR = new File(RESOURCES_DIR, ORG_CONTENTMINE_GRAPHICS + "math");
	public static final File LAYOUT_DIR = new File(RESOURCES_DIR, ORG_CONTENTMINE_GRAPHICS + "layout");
	
	public static final File IMAGES_DIR = new File(SVG_DIR, "images");
	public static final File IMAGE_G_2_2_SVG = new File(IMAGES_DIR, "image.g.2.2.svg");
	public static final File IMAGE_G_2_2_PNG = new File(IMAGES_DIR, "image.g.2.2.png");
	public static final File IMAGE_G_3_2_SVG = new File(IMAGES_DIR, "image.g.3.2.svg");
	public static final File IMAGE_G_8_0_SVG = new File(IMAGES_DIR, "image.g.8.0.svg");
	public static final File IMAGE_G_8_2_SVG = new File(IMAGES_DIR, "image.g.8.2.svg");
	
	public static final File OBJECTS_DIR = new File(SVG_DIR, "objects");
	
	public static final File PATHS_DIR = new File(SVG_DIR, "paths");
	public static final File PATHS_BMCLOGO_SVG = new File(PATHS_DIR, "bmclogo.svg");
	public static final File PATHS_NOPATH_SVG = new File(PATHS_DIR, "nopath.svg");
	public static final File PATHS_RECT_LINE_SVG = new File(PATHS_DIR, "rectLine.svg");
	public static final File PATHS_TEXT_LINE_SVG = new File(PATHS_DIR, "textLine.svg");
	public static final File PATHS_SIMPLE_TREE_SVG = new File(SVGHTMLFixtures.PATHS_DIR, "simpleTree.svg");

	public static final File CC0_SVG = new File(SVGHTMLFixtures.IMAGES_DIR, "cc0.png");
	public static final File CCBY_PNG = new File(SVGHTMLFixtures.IMAGES_DIR, "ccby.png");
	public static final File CHEM_BMP = new File(SVGHTMLFixtures.IMAGES_DIR, "chem.bmp");
	public static final File FIGSHARE1138891_PNG = new File(SVGHTMLFixtures.IMAGES_DIR, "figshare1138891.png");
	public static final File IMAGE_TEST_PNG = new File(SVGHTMLFixtures.IMAGES_DIR, "imageTest.png");

	public static final String MONOCHROME = "monochrome";

	public static final File MONOCHROME1_PNG = new File(SVGHTMLFixtures.IMAGES_DIR, "monochrome1.png");
	public static final File MONOCHROME2_PNG = new File(SVGHTMLFixtures.IMAGES_DIR, "monochrome2.png");
	public static final File MONOCHROME2PMRCC0_PNG = new File(SVGHTMLFixtures.IMAGES_DIR, "monochrome2pmrcc0.png");
	public static final File MONOCHROME2PUBDOM_PNG = new File(SVGHTMLFixtures.IMAGES_DIR, "monochrome2pubdom.png");
	public static final File MONOCHROME2PUBDOM_STREAM_PNG = new File(SVGHTMLFixtures.IMAGES_DIR, "monochrome2pubdomStream.png");
	public static final File MONOCHROME2TEXT_PNG = new File(SVGHTMLFixtures.IMAGES_DIR, "monochrome2text.png");
	public static final File PLOS_GRAPH_SVG = new File(SVGHTMLFixtures.IMAGES_DIR, "plosGraph.svg");
	public static final File PLOTS_PUBDOM_PNG = new File(SVGHTMLFixtures.IMAGES_DIR, "plotspubdom.png");
	public static final File PLOTS1_BMP = new File(SVGHTMLFixtures.IMAGES_DIR, "plots1.bmp");
	public static final File PLOTS_CC0_PNG = new File(SVGHTMLFixtures.IMAGES_DIR, "plotscc0.png");
	public static final File PMRCC0_PNG_ = new File(SVGHTMLFixtures.IMAGES_DIR, "pmrcc0.png");
	public static final File PUBDOM_PNG = new File(SVGHTMLFixtures.IMAGES_DIR, "pubdom.png");
	public static final File TEST_PNG = new File(SVGHTMLFixtures.IMAGES_DIR, "test.png");
	public static final File TEST1MINI_BMP = new File(SVGHTMLFixtures.IMAGES_DIR, "test1mini.bmp");

	public static final File SVG_G_8_0_SVG = new File(SVGHTMLFixtures.SVG_DIR, "image.g.8.0.svg");
	public static final File SVG_G_8_2_SVG = new File(SVGHTMLFixtures.SVG_DIR, "image.g.8.2.svg");
	public static final File SVG_PAGE6_SVG = new File(SVGHTMLFixtures.SVG_DIR, "page6.svg");

	public static final File BAR_DIR = new File(SVG_DIR, "bar");
	public static final File CORPUS_DIR = new File(SVG_DIR, "corpus");
	public static final File FIGURE_DIR = new File(SVG_DIR, "figure");
	public static final File FONTS_DIR = new File(SVG_DIR, "fonts");
	public static final File IMAGE_DIR = new File(SVG_DIR, "images");
	public static final File MATH_DIR = new File(SVG_DIR, "math");
	public static final File PAGE_DIR = new File(SVG_DIR, "page");
	public static final File PATH_DIR = new File(SVG_DIR, "path");
	public static final File PLOT_DIR = new File(SVG_DIR, "plot");
	public static final File TABLE_DIR = new File(SVG_DIR, "table");
	public static final File TEXT_DIR = new File(SVG_DIR, "text");
	
	public static final File LINEPLOTS_10_2_SVG = new File(PLOT_DIR, "lineplots.g.10.2.svg");
	public static final File SCATTERPLOT_FIVE_7_2_SVG = new File(PLOT_DIR, "scatterplot5.g.7.2.svg");
	public static final File SCATTERPLOT_7_2_SVG = new File(PLOT_DIR, "scatterplot.g.7.2.svg");
	
	public static final File FOREST_DIR = new File(PLOT_DIR, "forest");
	public static final File FUNNEL_DIR = new File(PLOT_DIR, "funnel");
	public static final File TEX_PLOT_DIR = new File(PLOT_DIR, "tex");
	public static final File BLKSAM_PLOT_DIR = new File(PLOT_DIR, "blk_sam");

	public static final File SVG_IMAGES_DIR = new File(SVG_DIR, "svgimages");
	
	public final static String IMAGE_SVG = ""
		 + "<svg xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\"><image  x=\"0.0\" y=\"0.0\" width=\"16.0\" height=\"16.0\" xlink:href=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAMklEQVR42mP4J8LwHx0zAAE2cWzyeBUSgxnw2UwMnzouINVmnF4YwmEwmg7Is3kYhQEA6pzZRchLX5wAAAAASUVORK5CYII=\"/></svg>";
	public final static File LARGE_IMAGE_SVG = new File(SVG_IMAGES_DIR, "multiple-image-page6.svg"); 
	public final static File LETTERA_SVG_FILE = new File(SVGHTMLFixtures.IMAGES_DIR, "lettera.svg");

	public final static File ROUNDED_LINE_SVG_FILE = new File(SVGHTMLFixtures.PATHS_DIR, "roundedline.svg");

	public static final File MOLECULES_DIR = new File(SVG_DIR, "molecules");
	public static final File IMAGE_2_13_SVG = new File(SVGHTMLFixtures.MOLECULES_DIR, "image.g.2.13.svg");
	public static final File IMAGE_2_11_NO2_SVG = new File(SVGHTMLFixtures.MOLECULES_DIR, "image.g.2.11.no2.svg");
	
	public static final File TABLE_LINE_DIR = new File(TABLE_DIR, "line");
	public static final File TABLE_RECT_DIR = new File(TABLE_DIR, "rect");
	public static final File TABLE_PAGE_DIR = new File(TABLE_DIR, "page");
	public static final File TABLE_PDF_DIR = new File(TABLE_DIR, "pdf");
	
	public static final File TARGET_TABLE_CACHE_DIR = new File("target/table/cache/");
	public static final File TABLE_TYPE_DIR = new File(TABLE_DIR, "types");
	public static final File TABLE_TYPE_APA_DIR = new File(TABLE_TYPE_DIR, "apa");
	public static final File TABLE_TYPE_APAROT_DIR = new File(TABLE_TYPE_DIR, "aparot");
	public static final File TABLE_TYPE_AUTHOR_DIR = new File(TABLE_TYPE_DIR, "author");
	public static final File TABLE_TYPE_BANDED_DIR = new File(TABLE_TYPE_DIR, "banded");
	public static final File TABLE_TYPE_GRIDDED_DIR = new File(TABLE_TYPE_DIR, "gridded");
	public static final File TABLE_TYPE_LEFTBAR_DIR = new File(TABLE_TYPE_DIR, "leftbar");
	public static final File TABLE_TYPE_PANEL_DIR = new File(TABLE_TYPE_DIR, "panel");
	public static final File TABLE_TYPE_RULES_DIR = new File(TABLE_TYPE_DIR, "rules");
	public static final File[] TABLE_TYPES = { 
			TABLE_TYPE_APA_DIR,
			TABLE_TYPE_APAROT_DIR,
			TABLE_TYPE_AUTHOR_DIR,
			TABLE_TYPE_BANDED_DIR,
			TABLE_TYPE_GRIDDED_DIR,
			TABLE_TYPE_LEFTBAR_DIR,
			TABLE_TYPE_PANEL_DIR,
			TABLE_TYPE_RULES_DIR,
		};

	

	public static final double EPS = 0.5;

	public static void writeImageQuietly(BufferedImage image, File file) {
		if (image == null) {
			throw new RuntimeException("Cannot write null image: "+file);
		}
		try {
			// DONT EDIT!
			String type = FilenameUtils.getExtension(file.getName());
			file.getParentFile().mkdirs();
			ImageIO.write(image, type, new FileOutputStream(file));
		} catch (Exception e) {
			throw new RuntimeException("cannot write image "+file, e);
		}
	}
	
	public static void cleanAndCopyDir(File sourceDir, File targetDir) {
		try {
			if (targetDir.exists()) FileUtils.forceDelete(targetDir);
			LOG.trace(sourceDir.getAbsolutePath());
			FileUtils.copyDirectory(sourceDir, targetDir);
		} catch (IOException ioe) {
			throw new RuntimeException("failed to clean and copy: "+sourceDir+" @ "+targetDir +": "+ioe, ioe);
		}
	}




}
