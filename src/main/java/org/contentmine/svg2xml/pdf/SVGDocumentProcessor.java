package org.contentmine.svg2xml.pdf;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.util.CMFileUtil;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.html.HtmlImg;
import org.contentmine.graphics.html.HtmlSpan;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.StyleBundle;
import org.contentmine.graphics.svg.StyleBundle.FontWeight;

import nu.xom.Attribute;


/** for SVG created from PDF2SVG2.
 * 
 * (this may not be the right package for this)
 * 
 * @author pm286
 *
 */
public class SVGDocumentProcessor {
	private static final Logger LOG = Logger.getLogger(SVGDocumentProcessor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static final String SUB_SECTION = "subSection";
	private static final String RUNNING = "running";
	private static final String PAGE = "page";
	private static final String SECTION = "section";
	private static final String FIGURE = "figure";
	private static final String TABLE = "table";
	private static final String IMAGE = "image";
	private static final String CLASS = "class";

	private static final String CSS_STYLE = ""
			+ ".section {"
			+ "background:#ffffdd;"
			+ "margin:3px;"
			+ "border-style:solid;"
			+ "border-width:0.5px;"
			+ "}"
			+ ".table {"
			+ "background:#ffddff;"
			+ "margin:3px;"
			+ "border-style:solid;"
			+ "border-width:0.5px;"
			+ "}"
			+ ".figure {"
			+ "background:#ddffff;"
			+ "margin:3px;"
			+ "border-style:solid;"
			+ "border-width:0.5px;"
			+ "}"
			+ ".image {"
			+ "background:#dddddd;"
			+ "margin:3px;"
			+ "border-style:solid;"
			+ "border-width:0.5px;"
			+ "font-size:30;"
			+ "}"
			;

   public static String[] SECTION_HEAD_LIST = {
   "abstract",
   "abbreviations:",
   "acknowledgements",
   "additional information",
   "authors",
   "competing financial interests",
   "competing interests",
   "conclusions",
   "conflicts of interest",
   "contributions",
   "correspondence",
   "corresponding authors",
   "discussion",
   "figure Legends",
   "funding",
   "introduction",
   "key words",
   "materials and methods",
   "methods",
   "notes and references",
   "references",
   "results",
   "results and Discussion",
   "sampling",
   "supporting information",
   };
   public final static String[] MINOR_SECTIONS ={
   "*corresponding author",
	};
   
   public static Pattern FIGURE_REGEX = Pattern.compile(
		   "^\\s*([Ss]upplementary)?\\s*[Ff]ig(\\.|ure)\\s*S?\\d+\\.?.*");
   public static Pattern TABLE_REGEX = Pattern.compile(
		   "^\\s*(S\\d+)?\\s*[Tt]able\\.?.*");
   //           S4 Table. Primers used in this study.

	private List<SVGSVG> sortedSvgPageList;
	private HtmlDiv subDiv;
	private HtmlDiv subSubDiv;
	private HtmlDiv currentDiv;
	private HtmlDiv pageDiv;
	private Double leftMarginX;
	private Double largeTextSize;

	public SVGDocumentProcessor() {
		init();
	}
	
	private void init() {
		leftMarginX = 80.;
		largeTextSize = 13.;
	}

	public List<SVGSVG> readSVGFilesIntoSortedPageList(List<File> svgFiles) {
		sortedSvgPageList = new ArrayList<SVGSVG>();
		svgFiles = CMFileUtil.sortUniqueFilesByEmbeddedIntegers(svgFiles);
		for (File svgFile : svgFiles) {
			SVGSVG svgPage = (SVGSVG) SVGElement.readAndCreateSVG(svgFile);
			sortedSvgPageList.add(svgPage);
		}
		return sortedSvgPageList;
	}

	public HtmlHtml readAndConvertToHtml(List<File> svgFiles) {
		readSVGFilesIntoSortedPageList(svgFiles);
		HtmlHtml html = new HtmlHtml();
		html.getOrCreateHead().addCssStyle(CSS_STYLE);
		for (SVGSVG svgPage : sortedSvgPageList) {
			HtmlDiv htmlPage = convertToHtml(svgPage);
			html.getOrCreateBody().appendChild(htmlPage);
		}
		return html;
	}


	public HtmlDiv convertToHtml(SVGSVG svgPage) {
		pageDiv = new HtmlDiv();
		pageDiv.setClassAttribute(PAGE);
		currentDiv = pageDiv;
		List<SVGText> textList = SVGText.extractSelfAndDescendantTexts(svgPage);
		textList = ignoreNegativeY(textList);
		textList = ignoreEmptyText(textList);
		textList = ignoreLineNumbers(textList);
		subDiv = new HtmlDiv();
		subDiv.setClassAttribute(RUNNING);
		pageDiv.appendChild(subDiv);
		for (SVGText text : textList) {
			text.format(3);
			if (addPageNumberToDiv(pageDiv, text) != null) continue;
			if (addImageNumberToDiv(currentDiv, text) != null) continue;
			if (addSubDiv(currentDiv,  FIGURE,   getHeading(text, 12.5, FIGURE_REGEX)) != null) continue;
			if (addSubDiv(currentDiv,  TABLE,    getHeading(text, 12.5, TABLE_REGEX))  != null) continue;
			if (addSubDiv(pageDiv, SECTION,  getHeading(text, 16.5, SECTION_HEAD_LIST)) != null) continue;
			if (addSubDiv(subDiv,  SUB_SECTION, getHeading(text, 12.5, MINOR_SECTIONS)) != null) continue;
			currentDiv.appendChild(createSpan(text));
		}
		return pageDiv;
	}

	private SVGText getHeading(SVGText text, double textSize, String[] leadingStrings) {
		Double x = text.getX();
		String textS = text.getText();
		if (x < leftMarginX) {
			Double fontSize = text.getFontSize();
			if (fontSize > textSize || 
					FontWeight.BOLD.toString().toLowerCase().equals(text.getFontWeight().toLowerCase())) {
				LOG.trace(fontSize + " section "+text.getText());
				return text;
			} 
			textS = textS.toLowerCase();
			for (String leadingString : leadingStrings) {
				if (textS.startsWith(leadingString)) {
					LOG.debug("START: "+textS);
					return text;
				}
			}
		}
		return null;
	}

	private SVGText getHeading(SVGText text, double textSize, Pattern pattern) {
		Double x = text.getX();
		String textS = text.getText();
		Matcher matcher = pattern.matcher(textS);
		if (x < leftMarginX) {
			if (matcher.matches()) {
				LOG.trace("TABFIG: "+textS);
				return text;
			}
		}
		return null;
	}

	private HtmlDiv addSubDiv(HtmlDiv parentDiv, String classAttribute, SVGText heading) {
		HtmlDiv subDiv = null;
		if (heading != null) {
			subDiv = new HtmlDiv();
			subDiv.setClassAttribute(classAttribute).setTitle(heading.getText());
			parentDiv.appendChild(subDiv);
			HtmlSpan span = createSpan(heading);
			subDiv.appendChild(span);
			currentDiv = subDiv;
		}
		return subDiv;
	}
	
	private Integer addPageNumberToDiv(HtmlDiv div, SVGText text) {
		String textS = text.getText();
		Integer number = null;
		if (textS != null) {
			textS = textS.trim();
			try {
				number = Integer.parseInt(textS);
				if (number > 0 && text.getY() > 730) {
					div.setTitle("page "+number);
					LOG.trace("number "+number);
				}
			} catch (Exception e) {
				// not a number
			}
		}
		return number;
	}

	private HtmlSpan addImageNumberToDiv(HtmlDiv div, SVGText text) {
		if (IMAGE.equals(text.getAttributeValue(CLASS))) {
			HtmlSpan span = createSpan(text);
			span.setStyle("font-size:10pt;");
			span.setClassAttribute(IMAGE);
			HtmlImg img = new HtmlImg();
			String textS = text.getText();
			// image.10.1[...]
			String pageSerialS = textS.substring("image.".length(), textS.indexOf("["));
			img.setSrc("../images/page."+pageSerialS+".png");
			div.appendChild(img);
			div.appendChild(span);
			return span;
		}
		return null;
	}

	private List<SVGText> ignoreLineNumbers(List<SVGText> textList) {
		List<SVGText> newTextList = new ArrayList<SVGText>();
		for (SVGText text : textList) {
			if (text.getX() < 50 && text.getText().trim().length() < 4) {
			} else {
				newTextList.add(text);
			}
		}
		return newTextList;
	}


	/** should go into a new SVGTextList object.
	 * 
	 * @param textList
	 * @return
	 */
	private List<SVGText> ignoreNegativeY(List<SVGText> textList) {
		List<SVGText> newTextList = new ArrayList<SVGText>();
		for (SVGText text : textList) {
			if (text.getY() > 0) {
				newTextList.add(text);
			}
		}
		return newTextList;
	}

	private List<SVGText> ignoreEmptyText(List<SVGText> textList) {
		List<SVGText> newTextList = new ArrayList<SVGText>();
		for (SVGText text : textList) {
			if (text != null && text.getText() != null && text.getText().length() > 0) {
				newTextList.add(text);
			}
		}
		return newTextList;
	}

	private HtmlSpan createSpan(SVGText text) {
		HtmlSpan span = new HtmlSpan();
		if (text != null) {
			span.appendChild(text.getText());
			StyleBundle bundle = text.getStyleBundle();
			span.setStyle(bundle.getCSSStyle());
			span.addAttribute(new Attribute("x", ""+text.getX()));
			span.addAttribute(new Attribute("y", ""+text.getY()));
		}
		return span;
	}
}
