package org.contentmine.graphics.svg.cache;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.html.HtmlBody;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.util.FilePathGlobber;

/** manages a complete corpus of several documents.
 * not suitable for large numbers
 * @author pm286
 *
 */
public class CorpusCache extends ComponentCache {
	
	private static final Logger LOG = Logger.getLogger(CorpusCache.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public static String DIR_REGEX = "(.*)/fulltext\\.(pdf|xml)";

	private File cProjectDir;
	private List<DocumentCache> documentCacheList;
	private List<File> cTreeFiles;

	private List<HtmlElement> htmlElementList;


	public CorpusCache() {
		
	}
	
	public CorpusCache(File cproject) {
		SVGG g = null;
		try {
			g = (SVGG) this.processCProject(cproject);
		} catch (IOException ioe) {
			throw new RuntimeException("Glob failed: "+ioe);
		}
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/demos/corpus.svg"), 200., 200.);
	}

	public AbstractCMElement processCProject(File cProjectDir) throws IOException {
		List<File> dirFiles = getChildCTrees(cProjectDir);
		LOG.debug(dirFiles.size()+"; "+dirFiles);
		this.setCProject(cProjectDir);
		getOrCreateDocumentCacheList();
		convertedSVGElement = new SVGG();
		convertedSVGElement.setFontSize(10.);
		double x = 10.0;
		double y = 20.0;
		double deltaY = 10.;
		int count = 0;
		for (File cTreeDir : dirFiles) {
			count++;
			LOG.debug("*****"+count+"*****making DocumentCache: "+cTreeDir+" ****************");
			DocumentCache documentCache = new DocumentCache(cTreeDir);
			documentCacheList.add(documentCache);
			HtmlElement htmlDiv = documentCache.getHtmlDiv();
			File file = new File(cTreeDir, "html/html.html");
			LOG.debug("WROTE: "+file);
			HtmlHtml.wrapAndWriteAsHtml(htmlDiv, file);
			convertedSVGElement.appendChild(new SVGText(new Real2(x, y), cTreeDir.getName()));
			y += deltaY;
		}
		return convertedSVGElement;
	}

	private List<File> getChildCTrees(File cProjectDir) throws IOException {
		FilePathGlobber globber = new FilePathGlobber();
		globber.setRegex(CorpusCache.DIR_REGEX)
		    .setUseDirectories(true)
		    .setLocation(cProjectDir.toString());
		cTreeFiles = globber.listFiles();
		return cTreeFiles;
	}

	public List<DocumentCache> getOrCreateDocumentCacheList() {
		if (documentCacheList == null) {
			documentCacheList = new ArrayList<DocumentCache>();
		}
		return documentCacheList;
	}

	private void setCProject(File cProjectDir) {
		this.cProjectDir = cProjectDir;
	}

	/** concatenates the documents as one huge HTML
	 * 
	 * @return
	 */
	public HtmlElement getOrCreateConvertedHtmlElement() {
		if (this.convertedHtmlElement == null) {
			this.convertedHtmlElement = new HtmlHtml();
			HtmlBody bodyAll = ((HtmlHtml)convertedHtmlElement).getOrCreateBody();
			for (DocumentCache documentCache : documentCacheList) {
				LOG.debug("hack this later");
//				HtmlElement element = documentCache.getOrCreateConvertedHtmlElement();
				
			}
		}
		return convertedHtmlElement;
	}

	public List<HtmlElement> getHtmlElementList() {
		if (this.htmlElementList == null) {
			this.htmlElementList = new ArrayList<HtmlElement>();
			for (DocumentCache documentCache : documentCacheList) {
				LOG.debug("********************Document: "+documentCache.getTitle()+"******************");
				HtmlElement element = documentCache.getOrCreateConvertedHtmlElement();
				if (element == null) {
					LOG.warn("Null html");
					throw new RuntimeException("Null element");
				} else {
					htmlElementList.add(element);
				}
			}
		}
		return htmlElementList;
	}



}
