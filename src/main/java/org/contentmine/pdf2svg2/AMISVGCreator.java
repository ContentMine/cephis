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

import javax.imageio.ImageIO;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.pdfbox.contentstream.PDFGraphicsStreamEngine;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
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
public class AMISVGCreator
{
	private static final Logger LOG = Logger.getLogger(AMISVGCreator.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private PDF2SVGParser svg2PDFParser;
	private PDDocument doc;
	private AMIPDFRenderer renderer;
	private BufferedImage renderedImage;
	private SVGG svgg;

	public static void main(String[] args) throws IOException
    {
        File file = new File("src/test/resources/org/contentmine/pdf2svg2/",
                "custom-render-demo.pdf");
        
        AMISVGCreator svgCreator = new AMISVGCreator();
        SVGG svgg = svgCreator.createSVG(file);
        SVGSVG.wrapAndWriteAsSVG(svgg, new File("target/pdf2svg2/examples/custom.svg"));
        BufferedImage renderedImage = svgCreator.getRenderedImage();
        ImageIO.write(renderedImage, "PNG", new File("custom-render.png"));
    }

	public SVGG createSVG(File file) throws InvalidPasswordException, IOException {
        createRenderedImage(file);
        svgg = extractSVGG();
        return svgg;
	}

	private SVGG extractSVGG() {
		this.svg2PDFParser = renderer.getPDF2SVGParser();
        svgg = svg2PDFParser.getSVGG();
		return svgg;
	}

	private BufferedImage createRenderedImage(File file) throws InvalidPasswordException, IOException {
		doc = PDDocument.load(file);
        renderer = new AMIPDFRenderer(doc);
        renderedImage = renderer.renderImage(0);
        doc.close();
        return renderedImage;
	}

	public BufferedImage getRenderedImage() {
		return renderedImage;
	}

}
