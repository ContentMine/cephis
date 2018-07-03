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
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.pdfbox.contentstream.PDFGraphicsStreamEngine;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.contentmine.cproject.files.CTree;
import org.contentmine.graphics.svg.SVGElement;
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
public class AMISVGCreator {
	private static final String PAGES = "pages";
	private static final Logger LOG = Logger.getLogger(AMISVGCreator.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private PDF2SVGParser pdf2svgParser;
	private PDDocument currentDoc;
	private AMIPDFRenderer renderer;
	private BufferedImage renderedImage;
	private SVGG currentSVGG;
	private File currentFile;
	private List<SVGG> svggList;
	private double fontScale;
	private List<BufferedImage> rawImageList;
	private List<BufferedImage> renderedImageList;
	private List<String> imageSerialList;

	public static void main(String[] args) throws IOException {
        pdfBox2Test();
    }

	private static void pdfBox2Test() throws IOException {
		File file = new File("src/test/resources" + "/org/contentmine/pdf2svg2" + "/",
                "custom-render-demo.pdf");
        
        AMISVGCreator svgCreator = new AMISVGCreator();
        SVGG svgg = svgCreator.createSVG(file);
        SVGSVG.wrapAndWriteAsSVG(svgg, new File("target/pdf2svg2/examples/custom.svg"));
        BufferedImage renderedImage = svgCreator.createRenderedImageList().get(0);
        ImageIO.write(renderedImage, "PNG", new File("target/pdf2svg2/examples/custom.ami.png"));
	}

	public SVGG createSVG(File file) throws IOException {
		this.currentFile = file;
        createRenderedImageList();
        currentSVGG = extractSVGG();
        return currentSVGG;
	}

	private SVGG extractSVGG() {
		this.pdf2svgParser = renderer.getPDF2SVGParser();
        currentSVGG = pdf2svgParser.getSVGG();
		return currentSVGG;
	}

	private List<BufferedImage> createRawImageList() {
		this.pdf2svgParser = renderer.getPDF2SVGParser();
        return pdf2svgParser.getRawImageList();
	}

	public List<BufferedImage> createRenderedImageList() throws InvalidPasswordException, IOException {
			return (currentFile == null) ? null : createRenderedImageList(currentFile);
	}

	/** reads PDF and extracts images and creates SVG.
	 * 
	 * @param file
	 * @return
	 * @throws InvalidPasswordException
	 * @throws IOException
	 */
	public List<BufferedImage> createRenderedImageList(File file) throws IOException {
		if (renderedImageList == null) {
			currentDoc = PDDocument.load(file);
			renderedImageList = new ArrayList<BufferedImage>();
			svggList = new ArrayList<SVGG>();
	        renderer = new AMIPDFRenderer(currentDoc);
	        rawImageList = new ArrayList<BufferedImage>();
	        imageSerialList = new ArrayList<String>();
	        for (int iPage = 0; iPage < currentDoc.getNumberOfPages(); iPage++) {
	        	System.out.print(">"+iPage);
	        	BufferedImage renderImage = renderer.renderImage(iPage);
				renderedImageList.add(renderImage);
				SVGG svgPage = extractSVGG();
				svggList.add(svgPage);
				List<BufferedImage> imageList = createRawImageList();
				for (int image = 0; image < imageList.size();image++) {
					imageSerialList.add(CTree.PAGE+CTree.DOT+iPage+CTree.DOT+CTree.IMAGE+CTree.DOT+image+CTree.DOT+CTree.PNG);
				}
				rawImageList.addAll(imageList);
	        }
	        currentDoc.close();
		}
        return renderedImageList;
	}

	public List<SVGG> getSVGPageList() {
		return svggList;
	}

	public AMISVGCreator setFile(File file) {
		this.currentFile = file;
		return this;
	}

	public void setFontScale(double fontScale) {
		this.fontScale = fontScale;
	}
	
	public List<BufferedImage> getRawImageList() {
		return rawImageList;
	}

	public void writeSVGPages(File parent) {
		List<SVGG> svgPageList = getSVGPageList();
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
}
