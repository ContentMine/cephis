package org.contentmine.pdf2svg2;

import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.rendering.PageDrawer;
import org.apache.pdfbox.rendering.PageDrawerParameters;

/** helper class extending PDFBox renderer
 * 
 * @author pm286
 *
 */
public class AMIPDFRenderer extends PDFRenderer {
	private static final Logger LOG = Logger.getLogger(AMIPDFRenderer.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private PDF2SVGParser pdf2svgParser;

	AMIPDFRenderer(PDDocument document) {
        super(document);
    }

    @Override
    protected PageDrawer createPageDrawer(PageDrawerParameters parameters) throws IOException {
        pdf2svgParser = new PDF2SVGParser(parameters);

        return pdf2svgParser;
    }
    
    public PDF2SVGParser getPDF2SVGParser() {
    	return pdf2svgParser;
    }

}
