package org.contentmine.graphics.layout;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.graphics.svg.cache.DocumentCache;
import org.contentmine.graphics.svg.cache.PageCache;
import org.contentmine.graphics.svg.layout.PubstyleManager;
import org.contentmine.graphics.svg.layout.SVGPubstyle;
import org.junit.Ignore;
import org.junit.Test;

@Ignore("This really should be in POM or CL")
public class PubstyleIT {
	
	private static final Logger LOG = Logger.getLogger(PubstyleIT.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
		public void testPubstyleCache() {
			PubstyleManager pubstyleManager = new PubstyleManager();
			SVGPubstyle pubstyle = pubstyleManager.getSVGPubstyleFromPubstyleName("bmc");
			int end = 99; // break after file-not-found
			int start = 1;
			File cProject = new File(SVGHTMLFixtures.CORPUS_DIR, "mosquitos");
			LOG.debug("CPROJECT: "+cProject);
	//		Assert.assertEquals(1,  cProject.get);
			String dirRoot = "12936_2017_Article_1948";
			File cTree = new File(cProject, dirRoot);
			String pageRoot =  "svg/fulltext-page";
			pubstyle.setEndPage(end);
			DocumentCache documentCache = new DocumentCache(cTree);
			end = documentCache.getPageCount();
			for (int page = start; page <= end; page++) {
				LOG.debug("===================== current "+page+"====================");
				pubstyle.setCurrentPage(page);
				File inputSvgFile = new File(cTree, pageRoot+page+".svg.compact.svg");
				if (!inputSvgFile.exists()) {
					LOG.debug("cannot find: "+inputSvgFile);
					LOG.debug("====================FINISHED=================");
					break;
				}
				SVGElement inputSVGElement = SVGElement.readAndCreateSVG(inputSvgFile);
				PageCache pageCache = new PageCache(documentCache);
				
				pageCache.readGraphicsComponentsAndMakeCaches(inputSVGElement);
				
				String topDirName = "target/pubstyle/";
				File file;
				file = pageCache.debugSvgElementToSVGFile(topDirName, dirRoot, page);
				file = pageCache.debugShapesToSVGFile(topDirName, dirRoot, page);
				file = pageCache.debugChunksToSVGFile(pubstyle, topDirName, dirRoot, page);
			}
		}

}
