package org.contentmine.svg2xml.pdf;

import java.io.File;

import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlFactory;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.svg2xml.SVG2XMLFixtures;
import org.junit.Assert;
import org.junit.Test;

public class SVGDocumentProcessorTest {

	@Test
	public void testJoinSpans() throws Exception {
		File joinSpansHtmlFile = new File(new File(SVG2XMLFixtures.SVG2XML_DIR, "pdf"), "joinSpans.html");
		HtmlElement joinSpansElement = new HtmlFactory().parse(joinSpansHtmlFile);
	}
	
	@Test
	public void testJoinSpansInSVGPage() throws Exception {
		File page5SvgFile = new File(new File(SVG2XMLFixtures.SVG2XML_DIR, "pdf"), "fulltext-page.5.svg");
		SVGSVG page5Svg = (SVGSVG) SVGElement.readAndCreateSVG(page5SvgFile);
		SVGDocumentProcessor documentProcessor = new SVGDocumentProcessor();
		HtmlDiv htmlPage = documentProcessor.convertToHtml(page5Svg);
		File htmloutFile = new File(SVG2XMLFixtures.HTMLOUTDIR, "joins.html");
		XMLUtil.debug(htmlPage, htmloutFile, 1);
		Assert.assertTrue("outfile", htmloutFile.exists());

	}
}
