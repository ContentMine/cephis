package org.contentmine.graphics.svg.cache;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.AbstractCMElement;
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

	private TextChunkCache() {
	}
	
	public TextChunkCache(ComponentCache containingComponentCache) {
		super(containingComponentCache);
	}

	public List<? extends SVGElement> getOrCreateElementList() {
		getOrCreateTextChunkList();
		List<TextChunk> textChunks = new ArrayList<TextChunk>();
		return textChunks;
	}
	
	public List<SVGText> getOrCreateRawTextList() {
		if (rawTextList == null) {
			getOrCreateSiblingTextCache();
			rawTextList = siblingTextCache.getOrCreateOriginalTextList();
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
			List<SVGText> textList = siblingTextCache.getOrCreateHorizontalTexts();
			textStructurer = TextStructurer.createTextStructurerWithSortedLines(textList);
		}
		return textStructurer;
	}

	private TextCache getOrCreateSiblingTextCache() {
		if (siblingTextCache == null) {
			siblingTextCache = this.getOwnerComponentCache().getOrCreateTextCache();
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
	
	


}
