package org.contentmine.pdf2svg2;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.rendering.PageDrawer;
import org.apache.pdfbox.rendering.PageDrawerParameters;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGText;

/** helper class extending PDFBox renderer
 * works with PageDrawer to capture the PDF input stream
 * 
 * @author pm286
 *
 */
public class DocumentParser extends PDFRenderer {
	private static final Logger LOG = Logger.getLogger(DocumentParser.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private PageParser currentPageParser;
	private SVGG currentSVGG;
	private Map<PageSerial, SVGG> svgPageBySerial;
	private Map<PageSerial, BufferedImage> rawImageBySerial;
	private Map<PageSerial, BufferedImage> renderedImageBySerial;
	private List<String> imageSerialList;
	private PageIncluder pageIncluder;


	DocumentParser(PDDocument document) {
        super(document);
        LOG.trace("created parserRenderer");
    }

	/** create pageParser, actually a subclassed PageDrawer.
	 * think of PageDrawer as a parser that routes graphics ot SVG and creation
	 * of bufferedImages. Never call this.
	 * 
	 */
    @Override
    protected PageDrawer createPageDrawer(PageDrawerParameters parameters) throws IOException {
        currentPageParser = new PageParser(parameters);
        LOG.trace("created PDF2SVGParserPageDrawer");
        return currentPageParser;
    }
    
    /** process the page - main entry point
     * 
     *  calls super.renderImage() 
     * 
     * @param iPage
     * @return
     */
	public void processPage(int iPage) {
		try {
			BufferedImage renderImage = super.renderImage(iPage);
			currentPageParser.setRenderedImage(renderImage);
			currentPageParser.setPageSerial(PageSerial.createFromZeroBasedPage(iPage));
		} catch (IOException e) {
			throw new RuntimeException("fails to parse page", e);
		}
	}
	
	public PageParser getPageParser() {
		return currentPageParser;
	}

	/** reads PDF and extracts images and creates SVG.
	 * 
	 * @param file
	 * @return
	 * @throws InvalidPasswordException
	 * @throws IOException
	 */
	public Map<PageSerial, BufferedImage> parseDocument(PDDocument currentDoc) throws IOException {
		renderedImageBySerial = new HashMap<PageSerial, BufferedImage>();
        imageSerialList = new ArrayList<String>();
        svgPageBySerial = new HashMap<PageSerial, SVGG>();
        rawImageBySerial = new HashMap<PageSerial, BufferedImage>();
        for (int iPage = 0; iPage < currentDoc.getNumberOfPages(); iPage++) {
			PageSerial pageSerial = PageSerial.createFromZeroBasedPage(iPage);
        	if (getOrCreatePageIncluder().pageIsIncluded(pageSerial)) {
	        	System.out.print("["+pageSerial.getOneBasedPage()+"]");
	        	this.processPage(iPage);
	        	BufferedImage renderedImage = currentPageParser.getRenderedImage();
				renderedImageBySerial.put(pageSerial, renderedImage);
				SVGG svgPage = extractSVGG();
				cleanUp(svgPage);
				svgPageBySerial.put(pageSerial, svgPage);
				List<BufferedImage> subImageList = currentPageParser.getOrCreateRawImageList();
				for (int subImage = 0; subImage < subImageList.size();subImage++) {
					rawImageBySerial.put(PageSerial.createFromZeroBasedPages(iPage, subImage),
							subImageList.get(subImage));
				}
        	}
        }
        return renderedImageBySerial;
	}
	
	/** reads PDF and extracts images and creates SVG.
	 * 
	 * @param file
	 * @return list of files (can be empty but not null)
	 * @throws InvalidPasswordException
	 * @throws IOException
	 */
	public List<SVGG> getOrCreateSVGPageList() {
		List<SVGG> pageList = null;
		if (svgPageBySerial != null) {
			pageList = new ArrayList<SVGG>(svgPageBySerial.values());
		} else {
			pageList = new ArrayList<SVGG>();
		}
        return pageList;
	}
	
	public Map<PageSerial, BufferedImage> getRawImageMap() {
		return rawImageBySerial;
	}

	public PageIncluder getOrCreatePageIncluder() {
		if (pageIncluder == null) {
			pageIncluder = new PageIncluder();
		}
		return pageIncluder;
	}

	private void cleanUp(SVGG svgPage) {
		List<SVGText> texts = SVGText.extractSelfAndDescendantTexts(svgPage);
		for (SVGText text : texts) {
			if (text.getText().startsWith(" ")) {
				text.removeCharacter(0);
			}
		}
	}

	private SVGG extractSVGG() {
        currentSVGG = currentPageParser.getSVGG();
        
		return currentSVGG;
	}

	public List<SVGG> getOrCreateSVGList() {
		return new ArrayList<SVGG>(svgPageBySerial.values());
	}

	public List<BufferedImage> getOrCreateRenderedImageList() {
		return new ArrayList<BufferedImage>(renderedImageBySerial.values());
	}

	public Map<PageSerial, SVGG> getOrCreateSvgPageBySerial() {
		return svgPageBySerial;
	}




}
