package org.contentmine.pdf2svg2;

import java.io.File;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.files.CTreeList;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.svg2xml.pdf.SVGDocumentProcessor;
import org.junit.Test;

import junit.framework.Assert;

public class BiorxivTest {
	private static final Logger LOG = Logger.getLogger(BiorxivTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testBiorxivMarchantia1() throws Exception {
		File sourceDir = SVGHTMLFixtures.BIORXIV_DIR;
		File targetDir = SVGHTMLFixtures.BIORXIV_TARGET_DIR;
		CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
		String fileroot = "103861";
		File file = new File(targetDir, fileroot+".full.pdf");
		Assert.assertTrue("target pdf "+targetDir, file.exists());
	    PDFDocumentProcessor documentProcessor = new PDFDocumentProcessor();
	    documentProcessor.readAndProcess(file);
	    documentProcessor.writeSVGPages(new File(targetDir, fileroot));
    	documentProcessor.writeRawImages(targetDir);
	}
	
	@Test
	public void testBiorxivMarchantia2HTML1() throws Exception {
		File sourceDir = SVGHTMLFixtures.BIORXIV_DIR;
		File targetDir = SVGHTMLFixtures.BIORXIV_TARGET_DIR;
		CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
		String fileroot = "103861";
		CTree cTree = new CTree(new File(targetDir, fileroot));
		List<File> svgFiles = cTree.getExistingSVGFileList();
		Assert.assertEquals(29,  svgFiles.size());
		SVGDocumentProcessor svgDocumentProcessor = new SVGDocumentProcessor();
		svgDocumentProcessor.readSVGFiles(svgFiles);
		HtmlDiv div = svgDocumentProcessor.readAndConvertToHtml(svgFiles);
		HtmlHtml html = new HtmlHtml();
		html.getOrCreateBody().appendChild(div);
		File htmlFile = new File(targetDir, fileroot+".html");
//		LOG.debug(htmlFile);
		XMLUtil.debug(html, htmlFile, 1);
		
	}
	
	@Test
	public void testBiorxivMarchantia2HTMLAll() throws Exception {
		File sourceDir = SVGHTMLFixtures.BIORXIV_DIR;
		File targetDir = SVGHTMLFixtures.BIORXIV_TARGET_DIR;
		CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
		CProject project = new CProject(targetDir);
		CTreeList cTreeList = project.getOrCreateCTreeList();
		for (CTree cTree : cTreeList) {
			List<File> svgFiles = cTree.getExistingSVGFileList();
			SVGDocumentProcessor svgDocumentProcessor = new SVGDocumentProcessor();
			svgDocumentProcessor.readSVGFiles(svgFiles);
			HtmlDiv div = svgDocumentProcessor.readAndConvertToHtml(svgFiles);
			HtmlHtml html = new HtmlHtml();
			html.getOrCreateBody().appendChild(div);
			File htmlFile = new File(targetDir, cTree.getName()+".html");
			XMLUtil.debug(html, htmlFile, 1);
		}
	}
	
	

}
