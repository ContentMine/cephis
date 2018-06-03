package org.contentmine.graphics.svg.cache;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.cache.DocumentCache;
import org.contentmine.graphics.svg.cache.PageLayout;
import org.contentmine.eucl.xml.XMLUtil;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

@Ignore("This really should be in POM or CL")

public class DocumentCacheIT {
public static final Logger LOG = Logger.getLogger(DocumentCacheIT.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	/** 9-page article.
	 * 
	 */
	public void testDocument() {
		DocumentCache documentCache = new DocumentCache();
		documentCache.setCreateSummaryBoxes(true);
		documentCache.processSVGInCTreeDirectory(new File(SVGHTMLFixtures.G_S_PAGE_DIR, "varga1"));
		// superimposed pages
		SVGElement g = documentCache.getOrCreateConvertedSVGElement();
		Assert.assertNotNull("non-null g", g);
		Assert.assertTrue("empty g", g.getChildCount() > 0);
		File file = new File("target/document/varga/boxes.svg");
		LOG.debug("wrote: "+file.getAbsolutePath());
		SVGSVG.wrapAndWriteAsSVG(g, file);
		Assert.assertTrue("file exists: "+file, file.exists());
	}
	
	@Test 
	// FIXME null pointer
//	@Ignore
	public void testPageComponents() {
		
		DocumentCache documentCache = new DocumentCache();
		LOG.warn("incomplete, pageLayout not fixed");
		documentCache.analyzePages(SVGHTMLFixtures.G_S_PAGE_DIR, PageLayout.AMSOCGENE_RESOURCE, 9, "varga1/", new File("target/cache"));
//		documentCache.analyzePages(PageLayout.BMC, 8, "bmc/1471-2148-11-329/", new File("target/cache"));
//		documentCache.analyzePages(PageLayout.PLOSONE2016, 15, "TimmermansPLOS/", new File("target/cache"));
	}
	

	@Test
	public void testCreateHTMLPageAllCrop() throws IOException {
		File targetDir = new File("target/document/varga1");
		SVGHTMLFixtures.cleanAndCopyDir(new File(SVGHTMLFixtures.G_S_PAGE_DIR, "varga1/"), targetDir);
		DocumentCache documentCache = new DocumentCache(targetDir);
		documentCache.processSVGInCTreeDirectory(targetDir);
		XMLUtil.debug(documentCache.getHtmlDiv(), new File("target/html/pages.html"), 1);

	}
	
	@Test
	public void testCropBMC() throws IOException {
		// 295
		File targetDir = new File("target/document/bmc/12936_2017_Article_1948/");
		SVGHTMLFixtures.cleanAndCopyDir(new File(SVGHTMLFixtures.G_S_CORPUS_DIR, "mosquitos/12936_2017_Article_1948/"), targetDir);
		DocumentCache documentCache = new DocumentCache(targetDir);
		documentCache.processSVGInCTreeDirectory(targetDir);
		XMLUtil.debug(documentCache.getHtmlDiv(), new File(targetDir, "pages.html"), 1);
	}
	


}
