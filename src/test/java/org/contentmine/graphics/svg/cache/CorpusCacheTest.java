package org.contentmine.graphics.svg.cache;

import java.io.File;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.graphics.svg.cache.CorpusCache;
import org.contentmine.graphics.svg.cache.DocumentCache;
import org.junit.Ignore;
import org.junit.Test;

import junit.framework.Assert;

public class CorpusCacheTest {
	private static final Logger LOG = Logger.getLogger(CorpusCacheTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	@Ignore // toolong // FIXME
	public void testCorpusCache() {
		File corpusDir = new File(SVGHTMLFixtures.G_S_CORPUS_DIR, "mosquitos/");
		if (!corpusDir.exists()) {
			LOG.info("directory not found: "+corpusDir);
			return;
		}
		CorpusCache corpusCache = new CorpusCache(corpusDir);
		List<DocumentCache> documentCacheList = corpusCache.getOrCreateDocumentCacheList();
		// gets this wrong (returns 985??)
		Assert.assertEquals("doc cache",  10, documentCacheList.size());
		DocumentCache docCache0 = documentCacheList.get(0);
		LOG.trace(docCache0);
		LOG.trace("MADE CORPUS");
		List<HtmlElement> htmlElementList = corpusCache.getHtmlElementList();
		Assert.assertEquals("html files ", 4, htmlElementList.size());
		HtmlHtml.wrapAndWriteAsHtml(htmlElementList, corpusDir);
	}
	@Test
	@Ignore // toolong // FIXME
	public void testCorpusCache1() {
		File corpusDir = new File(SVGHTMLFixtures.G_S_CORPUS_DIR, "mosquitos1/");
		if (!corpusDir.exists()) {
			LOG.info("directory not found: "+corpusDir);
			return;
		}
		CorpusCache corpusCache = new CorpusCache(corpusDir);
		List<DocumentCache> documentCacheList = corpusCache.getOrCreateDocumentCacheList();
		// gets this wrong (returns 985??)
		Assert.assertEquals("doc cache",  10, documentCacheList.size());
		DocumentCache docCache0 = documentCacheList.get(0);
		LOG.trace(docCache0);
		LOG.trace("MADE CORPUS");
		List<HtmlElement> htmlElementList = corpusCache.getHtmlElementList();
		Assert.assertEquals("html files ", 4, htmlElementList.size());
		HtmlHtml.wrapAndWriteAsHtml(htmlElementList, corpusDir);
	}
}
