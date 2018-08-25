package org.contentmine.svg2xml.pdf;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlSpan;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.StyleBundle;

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

	public SVGDocumentProcessor() {
		
	}

	public void readSVGFiles(List<File> svgFiles) {
		List<SVGSVG> svgPageList = new ArrayList<SVGSVG>();
		for (File svgFile : svgFiles) {
			SVGSVG svgPage = (SVGSVG) SVGElement.readAndCreateSVG(svgFile);
			svgPageList.add(svgPage);
		}
	}

	public HtmlDiv readAndConvertToHtml(List<File> svgFiles) {
		readSVGFiles(svgFiles);
		HtmlDiv div = convertToHtmlDiv(svgFiles);
		return div;
	}

	public HtmlDiv convertToHtmlDiv(List<File> svgFiles) {
		HtmlDiv totalDiv = new HtmlDiv();
		for (File svgFile : svgFiles) {
			HtmlDiv div = convertToHtml(svgFile);
			totalDiv.appendChild(div);
		}
		return totalDiv;
	}

	public HtmlDiv convertToHtml(File svgFile) {
		SVGSVG svgPage = (SVGSVG) SVGElement.readAndCreateSVG(svgFile);
		HtmlDiv div = convertToHtml(svgPage);
		return div;
	}

	public HtmlDiv convertToHtml(SVGSVG svgPage) {
		HtmlDiv div = new HtmlDiv();
		List<SVGText> textList = SVGText.extractSelfAndDescendantTexts(svgPage); 
		for (SVGText text : textList) {
			text.format(3);
			HtmlSpan span = createSpan(text);
			div.appendChild(span);
		}
		return div;
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
