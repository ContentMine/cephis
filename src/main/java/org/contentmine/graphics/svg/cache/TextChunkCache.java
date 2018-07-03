package org.contentmine.graphics.svg.cache;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.html.HtmlP;
import org.contentmine.graphics.html.HtmlSpan;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.text.build.TextChunk;
import org.contentmine.graphics.svg.text.build.TextChunkList;
import org.contentmine.graphics.svg.text.structure.TextStructurer;

/** creates textChunks 
 * uses TextCache as raw input and systematically builds PhraseList and TextChunks
 * NOT FINISHED
 * 
 * @author pm286
 *
 */
public class TextChunkCache extends AbstractCache {
	static final Logger LOG = Logger.getLogger(TextChunkCache.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private List<SVGText> rawTextList;
	private TextChunkList textChunkList;
	private TextCache siblingTextCache;
	private TextStructurer textStructurer;
	private boolean omitWhitespace;
	private double paraSepRatio;

	private TextChunkCache() {
		init();
	}
	
	public TextChunkCache(ComponentCache containingComponentCache) {
		super(containingComponentCache);
		init();
	}

	private void init() {
		setDefaults();
	}

	private void setDefaults() {
		paraSepRatio = 1.7; 
	}

	public List<? extends SVGElement> getOrCreateElementList() {
		getOrCreateTextChunkList();
		List<TextChunk> textChunks = new ArrayList<TextChunk>();
		return textChunks;
	}
	
	public List<SVGText> getOrCreateRawTextList() {
		if (rawTextList == null) {
			TextCache siblingTextCache = getOrCreateSiblingTextCache();
			rawTextList = siblingTextCache == null ? null : siblingTextCache.getOrCreateOriginalTextList();
		}
		return rawTextList;
	}

	public TextChunkList getOrCreateTextChunkList() {
		if (textChunkList == null) {
			getOrCreateRawTextList();
			getOrCreateTextStructurer();
			textChunkList = textStructurer.getOrCreateTextChunkListFromWords();
//			LOG.debug("TC"+textChunkList);
//			throw new RuntimeException("TextChunks NYI");
		}
		return textChunkList;
	}

	@Override
	public String toString() {
		getOrCreateTextChunkList();
		String s = ""
			+ "rawText size: "+getOrCreateRawTextList().size()
			+ "textChunks: "+textChunkList.size()+"; "
			;
		return s;

	}

	@Override
	public void clearAll() {
		superClearAll();
		textChunkList = null;
	}

	/** sets bbox caching on all contained TextChunks.
	 * 
	 * @param boundingBoxCached 
	 */
	public void setBoundingBoxCached(boolean boundingBoxCached) {
		getOrCreateTextChunkList();
		for (TextChunk textChunk : textChunkList) {
			textChunk.setBoundingBoxCached(boundingBoxCached);
		}
	}
	
	/** if there is exactly one TextChunk, returns it.
	 * convenience method for single textChunk cases that avoids 
	 * for loop or if test
	 * @return the single textChunk or null.
	 */
	public TextChunk getSingleTextChunk() {
		getOrCreateTextChunkList();
		return textChunkList.size() != 1 ? null : textChunkList.get(0);
	}

	/** create a TextStructurer.
	 * maybe temporary if functions get transferred to TextChunkCache
	 * 
	 * @return noew or existoin TS
	 */
	public TextStructurer getOrCreateTextStructurer() {
		if (textStructurer == null) {
			getOrCreateSiblingTextCache();
			List<SVGText> textList = siblingTextCache == null ? null : siblingTextCache.getOrCreateHorizontalTexts();
			textStructurer = /*textList == null ? null : */TextStructurer.createTextStructurerWithSortedLines(textList);
		}
		return textStructurer;
	}

	private TextCache getOrCreateSiblingTextCache() {
		if (siblingTextCache == null) {
			ComponentCache ownerComponentCache = this.getOwnerComponentCache();
			siblingTextCache = ownerComponentCache == null ? null : ownerComponentCache.getOrCreateTextCache();
		}
		return siblingTextCache;
	}

	void cleanChunk(AbstractCMElement chunk) {
		if (omitWhitespace) {
			detachWhitespaceTexts(chunk);
		}
	}

	private void detachWhitespaceTexts(AbstractCMElement chunk) {
		List<SVGText> spaceList = SVGText.extractSelfAndDescendantTexts(chunk);
		for (SVGText text : spaceList) {
			String textS = text.getText();
			if (textS == null || textS.trim().length() == 0) {
				text.detach();
			}
		}
	}

	public TextChunkList getTextChunkList() {
		return textChunkList;
	}

	public void setTextChunkList(TextChunkList textChunkList) {
		this.textChunkList = textChunkList;
	}

	public void setTextStructurer(TextStructurer textStructurer) {
		this.textStructurer = textStructurer;
	}

	public boolean isOmitWhitespace() {
		return omitWhitespace;
	}

	public void setOmitWhitespace(boolean omitWhitespace) {
		this.omitWhitespace = omitWhitespace;
	}
	
	/** needs integrating with current textList.
	 * 
	 * @param textList
	 * @return
	 */
	public HtmlHtml createHtmlFromPage(List<SVGText> textList) {
	
		HtmlHtml html = new HtmlHtml();
		HtmlDiv div = new HtmlDiv();
		html.getOrCreateBody().appendChild(div);
		SVGText lastText = null;
		HtmlP p = null;
		for (SVGText text : textList) {
			if (lastText == null) {
				p = addNewPara(div);
			} else {
				double dy = text.getY() - lastText.getY();
				double fontSize = lastText.getFontSize();
				if (dy > fontSize * paraSepRatio) {
					p = addNewPara(div);
				}
			}
			lastText = text;
			addTextToSpanToP(p, " "); // interword space
			addTextToSpanToP(p, text.getText());
		}
		return html;
	}

	
	private static void addTextToSpanToP(HtmlP p, String text2) {
		HtmlSpan span = new HtmlSpan();
		span.appendChild(text2);
		p.appendChild(span);
	}

	private static HtmlP addNewPara(HtmlDiv div) {
		HtmlP p = new HtmlP();
		div.appendChild(p);
		return p;
	}

	public double getParaSepRatio() {
		return paraSepRatio;
	}

	public void setParaSepRatio(double paraSepRatio) {
		this.paraSepRatio = paraSepRatio;
	}



}
