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
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.pdfbox.contentstream.PDFGraphicsStreamEngine;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.contentmine.cproject.files.CTree;
import org.contentmine.eucl.euclid.Int2;
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
	
	private PageParser pageParser;
	private PDDocument currentDoc;
	private DocumentParser documentParser;
	private File currentFile;
	private Int2 minImageBox;

	public PDFDocumentProcessor() {
		init();
	}
	
	private void init() {
	}
	

	public PDFDocumentProcessor readAndProcess(File file) throws IOException {
		if (file != null && file.exists() && !file.isDirectory()) {
			readDocument(file);
			getOrCreateDocumentParser();
	        documentParser.parseDocument(currentDoc);
	        currentDoc.close();
		}
        return this;
	}

	private DocumentParser getOrCreateDocumentParser() {
		if (documentParser == null) {
			documentParser = new DocumentParser(currentDoc);
		}
		documentParser.getOrCreatePageIncluder();
		return documentParser;
	}

	private PDDocument readDocument(File file) throws IOException {
		this.currentFile = file;
		currentDoc = PDDocument.load(file);
		return currentDoc;
	}


	private List<BufferedImage> createRawSubImageList() {
		this.pageParser = documentParser.getPageParser();
        return pageParser.getOrCreateRawImageList();
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

	public void writeSVGPages(File parent) {
		File svgDir = new File(parent, CTree.SVG + "/");
		LOG.debug("writing to: "+svgDir);
		for (Map.Entry<PageSerial, SVGG> entry : documentParser.getOrCreateSvgPageBySerial().entrySet()) {
			PageSerial key = entry.getKey();
			SVGSVG.wrapAndWriteAsSVG(entry.getValue(), new File(svgDir, 
			""+key.getZeroBasedSerialString()+CTree.DOT+CTree.SVG));
		}
	}

	/** creates images from content
	 * probably not mainstream
	 * 
	 * @param parent
	 * @throws IOException
	 */
	public void writePageImages(File parent) throws IOException {
		List<BufferedImage> imageList = documentParser.getOrCreateRenderedImageList();
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
		Map<PageSerial, BufferedImage> rawImageByPageSerial = documentParser.getRawImageMap();
		for (PageSerial pageSerial : rawImageByPageSerial.keySet()) {
			BufferedImage image = rawImageByPageSerial.get(pageSerial);
			if (image.getHeight() >= minImageBox.getX() || image.getHeight() >= minImageBox.getY()) {
				ImageIO.write(image, CTree.PNG, 
					new File(imagesDir, "page."+pageSerial.getOneBasedSerialString()+"."+CTree.PNG));
			}
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

	/** tests against regression.
	 * 
	 * @throws IOException
	 */
	private static void pdfBox2Test() throws IOException {
		File file = new File("src/test/resources" + "/org/contentmine/pdf2svg2" + "/",
                "custom-render-demo.pdf");
        
        PDFDocumentProcessor documentProcessor = new PDFDocumentProcessor();
        documentProcessor.readAndProcess(file);
       List<SVGG> pages = documentProcessor.getOrCreateSVGPageList();
        SVGSVG.wrapAndWriteAsSVG(pages, new File("target/pdf2svg2/examples/custom.svg"));
        BufferedImage renderedImage = documentProcessor.getRenderedImageList().get(0);
        ImageIO.write(renderedImage, "PNG", new File("target/pdf2svg2/examples/custom.ami.png"));
	}

	private List<BufferedImage> getRenderedImageList() {
		return getOrCreateDocumentParser().getOrCreateRenderedImageList();
	}

	public List<SVGG> getOrCreateSVGPageList() {
		return getOrCreateDocumentParser().getOrCreateSVGList();
	}

	public PageIncluder getOrCreatePageIncluder() {
		return getOrCreateDocumentParser().getOrCreatePageIncluder();
	}

	/** smallest box allowed for images.
	 * 
	 * image will be rejected if fits within the box
	 * @param i
	 * @param j
	 */
	public void setMinimumImageBox(int width, int height) {
		this.minImageBox = new Int2(width, height);
	}

}
