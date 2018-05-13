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

	public static void main(String[] args) throws IOException
    {
        File file = new File("src/test/resources/org/contentmine/pdf2svg2/",
                "custom-render-demo.pdf");
        
        AMISVGCreator svgCreator = new AMISVGCreator();
        svgCreator.createSVG(file);
    }

	private PDF2SVGParser svg2PDFParser;

	public void createSVG(File file) throws InvalidPasswordException, IOException {
        PDDocument doc = PDDocument.load(file);
        AMIPDFRenderer renderer = new AMIPDFRenderer(doc);
        BufferedImage image = renderer.renderImage(0);
        ImageIO.write(image, "PNG", new File("custom-render.png"));
        this.svg2PDFParser = renderer.getPDF2SVGParser();
        SVGG svgg = svg2PDFParser.getSVGG();
        SVGSVG.wrapAndWriteAsSVG(svgg, new File("target/pdf2svg2/examples/custom.svg"));
        doc.close();
	}

}
