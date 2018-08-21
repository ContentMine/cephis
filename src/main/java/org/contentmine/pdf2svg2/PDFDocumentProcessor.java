/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.contentmine.pdf2svg2;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import javax.imageio.ImageIO;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.pdfbox.contentstream.PDFGraphicsStreamEngine;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.contentmine.cproject.files.CTree;
import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGSVG;

/**
 * Example showing custom rendering by subclassing PageDrawer.
 * 
 * <p>If you want to do custom graphics processing rather than Graphics2D rendering, then you should
 * subclass {@link PDFGraphicsStreamEngine} instead. Subclassing PageDrawer is only suitable for
 * cases where the goal is to render onto a Graphics2D surface.
 *
 * @author John Hewson
 * @author P Murray-Rust
 */
public class PDFDocumentProcessor {
	private static final String PAGES = "pages";
	private static final Logger LOG = Logger.getLogger(PDFDocumentProcessor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private PDF2SVGParserPageDrawer pdf2svgParserDrawer;
	private PDDocument currentDoc;
	private AMIPDFParserRenderer parserRenderer;
	private SVGG currentSVGG;
	private File currentFile;
	private List<SVGG> svgPageList;
	private List<BufferedImage> rawImageList;
	private List<BufferedImage> renderedImageList;
	private List<String> imageSerialList;
	private TreeSet<Integer> includeSortedPageNumbers;
	private TreeSet<Integer> excludeSortedPageNumbers;

	public PDFDocumentProcessor() {
		init();
	}
	
	private void init() {
	}
	

	public PDFDocumentProcessor readAndProcess(File file) throws IOException {
		if (file != null && file.exists() && !file.isDirectory()) {
			readDocument(file);
//	        parserRenderer = new AMIPDFParserRenderer(currentDoc);
	        createRenderedImageList();
//			getOrCreateSVGPageList();
		}
        return this;
	}

	private PDDocument readDocument(File file) throws IOException {
		this.currentFile = file;
		currentDoc = PDDocument.load(file);
		return currentDoc;
	}

	private SVGG extractSVGG() {
		if (parserRenderer == null) {
			LOG.error("no parser renderer");
			return null;
		}
		this.pdf2svgParserDrawer = parserRenderer.getPDF2SVGParserPageDrawer();
		if (pdf2svgParserDrawer == null) {
			LOG.error("no parser drawer");
			return null;
		}
        currentSVGG = pdf2svgParserDrawer.getSVGG();
		return currentSVGG;
	}

	private List<BufferedImage> createRawImageList() {
		this.pdf2svgParserDrawer = parserRenderer.getPDF2SVGParserPageDrawer();
        return pdf2svgParserDrawer.getRawImageList();
	}

	/** reads PDF and extracts images and creates SVG.
	 * 
	 * @param file
	 * @return
	 * @throws InvalidPasswordException
	 * @throws IOException
	 */
	public List<BufferedImage> createRenderedImageList() throws IOException {
		if (renderedImageList == null) {
			if (updateCurrentDoc() != null) {
				renderedImageList = new ArrayList<BufferedImage>();
		        parserRenderer = new AMIPDFParserRenderer(currentDoc);
		        rawImageList = new ArrayList<BufferedImage>();
		        imageSerialList = new ArrayList<String>();
		        List<SVGG> svgPageList = new ArrayList<SVGG>();
		        for (int iPage = 0; iPage < currentDoc.getNumberOfPages(); iPage++) {
		        	System.out.print(">"+iPage);
		        	BufferedImage renderImage = parserRenderer.renderImage(iPage);
					renderedImageList.add(renderImage);
					SVGG svgPage = extractSVGG();
					svgPageList.add(svgPage);
					List<BufferedImage> imageList = createRawImageList();
					for (int image = 0; image < imageList.size();image++) {
						imageSerialList.add(CTree.PAGE+CTree.DOT+iPage+CTree.DOT+CTree.IMAGE+CTree.DOT+image+CTree.DOT+CTree.PNG);
					}
					rawImageList.addAll(imageList);
		        }
		        currentDoc.close();
			}
		}
        return renderedImageList;
	}

	/** reads PDF and extracts images and creates SVG.
	 * 
	 * @param file
	 * @return
	 * @throws InvalidPasswordException
	 * @throws IOException
	 */
	public List<SVGG> getOrCreateSVGPageList() {
		if (svgPageList == null) {
			if (updateCurrentDoc() != null) {
				svgPageList = new ArrayList<SVGG>();
		        parserRenderer = new AMIPDFParserRenderer(currentDoc);
		        imageSerialList = new ArrayList<String>();
		        // pages count from 1, not zero
		        for (int iPage = 1; iPage <= currentDoc.getNumberOfPages(); iPage++) {
		        	LOG.debug(">>"+iPage);
		        	if (pageIsIncluded(iPage)) {
			        	System.out.print(">"+iPage);
						SVGG svgPage = extractSVGG();
						if (svgPage == null) {
							svgPage = new SVGG("page");
						}
						svgPageList.add(svgPage);
		        	}
		        }
		        try {
					currentDoc.close();
				} catch (IOException e) {
					throw new RuntimeException("Cannot close document", e);
				}
			}
		}
        return svgPageList;
	}

	/** include page by number.
	 * 
	 * if (pages have been included) return true if in list else false
	 * else if (pages have been excluded) return false if in list else true
	 * else return true
	 * 
	 * @param iPage
	 * @return
	 */
	private boolean pageIsIncluded(Integer iPage) {
		boolean include = true;
		if (includeSortedPageNumbers != null) {
			include = includeSortedPageNumbers.contains(iPage);
		} else if (excludeSortedPageNumbers != null) {
			include = excludeSortedPageNumbers.contains(iPage);
		}
		if (!include) {
			LOG.info("excluded page "+iPage);
		}
		return include;
	}

	private PDDocument updateCurrentDoc() {
		if (currentDoc == null && currentFile != null) {
			try {
				currentDoc = PDDocument.load(currentFile);
			} catch (IOException e) {
				throw new RuntimeException("Cannot read currentFile", e);
			}
		}
		return currentDoc;
	}

	public PDFDocumentProcessor setFile(File file) {
		this.currentFile = file;
		return this;
	}

	public List<BufferedImage> getRawImageList() {
		return rawImageList;
	}

	public void writeSVGPages(File parent) {
		List<SVGG> svgPageList = getOrCreateSVGPageList();
		File svgDir = new File(parent, CTree.SVG + "/");
		for (int i = 0; i < svgPageList.size(); i++) {
			SVGSVG.wrapAndWriteAsSVG(svgPageList.get(i), new File(svgDir, 
					CTree.createNumberedFullTextPageBasename(i)+CTree.DOT+CTree.SVG));
		}
	}

	/** creates images from content
	 * probably not mainstream
	 * 
	 * @param parent
	 * @throws IOException
	 */
	public void writePageImages(File parent) throws IOException {
		List<BufferedImage> imageList = createRenderedImageList();
		File pageDir = new File(parent, CTree.PAGES + "/");
		pageDir.mkdirs();
		for (int i = 0; i < imageList.size(); i++) {
			BufferedImage im = imageList.get(i);
			if (im != null) {
				ImageIO.write(im, CTree.PNG, new File(pageDir, 
						CTree.createNumberedFullTextPageBasename(i)+CTree.DOT+CTree.PNG));
			}
		}
	}

	public void writeRawImages(File parent) throws IOException {
		File imagesDir = new File(parent, CTree.IMAGES + "/");
		imagesDir.mkdirs();
		List<BufferedImage> rawImageList = getRawImageList();
		if (rawImageList.size() != imageSerialList.size()) {
			throw new RuntimeException("inconsistent raw "+rawImageList.size()+" serials "+imageSerialList);
		}
		for (int i = 0; i < rawImageList.size(); i++) {
			ImageIO.write(rawImageList.get(i), CTree.PNG, new File(imagesDir, imageSerialList.get(i)));
		}
	}
	
	/** this runs a test for sanity checking
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
        pdfBox2Test();
    }

	private static void renderDoc(File file, String path, int[] pages) throws InvalidPasswordException, IOException {
        File targetDir = new File("target/pdf2svg2/", path);
        targetDir.mkdirs();
		PDDocument doc = PDDocument.load(new File(file, path+".pdf"));
        PDFRenderer renderer = new AMIPDFParserRenderer(doc);
        for (int i : pages) {
        	LOG.debug(">> "+i);
//	        PDFRenderer renderer = new MyPDFRenderer(doc);
	        BufferedImage image = renderer.renderImage(i);
			ImageIO.write(image, "PNG", new File(targetDir, "page"+i+".png"));
        }
        doc.close();
	}


	private static void pdfBox2Test() throws IOException {
		File file = new File("src/test/resources" + "/org/contentmine/pdf2svg2" + "/",
                "custom-render-demo.pdf");
        
        PDFDocumentProcessor documentProcessor = new PDFDocumentProcessor();
        documentProcessor.readAndProcess(file);
        List<SVGG> pages = documentProcessor.getOrCreateSVGPageList();
        SVGSVG.wrapAndWriteAsSVG(pages, new File("target/pdf2svg2/examples/custom.svg"));
        BufferedImage renderedImage = documentProcessor.createRenderedImageList().get(0);
        ImageIO.write(renderedImage, "PNG", new File("target/pdf2svg2/examples/custom.ami.png"));
	}

	public PDFDocumentProcessor addIncludePages(List<Integer> includePages) {
		getOrCreateIncludePageList().addAll(includePages);
		return this;
	}

	public PDFDocumentProcessor addIncludePages(IntRange includePages) {
		List<Integer> pages = includePages.createArray().getIntegerList();
		return addIncludePages(pages);
	}

	public PDFDocumentProcessor addIncludePage(Integer ...includePages) {
		getOrCreateIncludePageList().addAll(new ArrayList<Integer>(Arrays.asList(includePages)));
		return this;
	}

	public TreeSet<Integer> getOrCreateIncludePageList() {
		if (includeSortedPageNumbers == null) {
			this.includeSortedPageNumbers = new TreeSet<Integer>();
		}
		return includeSortedPageNumbers;
	}

	public PDFDocumentProcessor addExcludePages(List<Integer> excludePages) {
		getOrCreateExcludePageList().addAll(excludePages);
		return this;
	}

	public PDFDocumentProcessor addExcludePages(IntRange excludePages) {
		List<Integer> pages = excludePages.createArray().getIntegerList();
		return addExcludePages(pages);
	}

	public PDFDocumentProcessor addExcludePage(int excludePage) {
		getOrCreateExcludePageList().add(excludePage);
		return this;
	}

	public TreeSet<Integer> getOrCreateExcludePageList() {
		if (excludeSortedPageNumbers == null) {
			this.excludeSortedPageNumbers = new TreeSet<Integer>();
		}
		return excludeSortedPageNumbers;
	}

}
