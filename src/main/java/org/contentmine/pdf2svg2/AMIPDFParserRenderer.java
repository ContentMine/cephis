package org.contentmine.pdf2svg2;

import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.rendering.PageDrawer;
import org.apache.pdfbox.rendering.PageDrawerParameters;

/** helper class extending PDFBox renderer
 * works with PageDrawer to capture the PDF input stream
 * 
 * @author pm286
 *
 */
public class AMIPDFParserRenderer extends PDFRenderer {
	private static final Logger LOG = Logger.getLogger(AMIPDFParserRenderer.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private PDF2SVGParserPageDrawer pdf2svgParserPageDrawer;

	AMIPDFParserRenderer(PDDocument document) {
        super(document);
        LOG.trace("created parserRenderer");
    }

    @Override
    protected PageDrawer createPageDrawer(PageDrawerParameters parameters) throws IOException {
        pdf2svgParserPageDrawer = new PDF2SVGParserPageDrawer(parameters);
        LOG.trace("created PDF2SVGParserPageDrawer");
        return pdf2svgParserPageDrawer;
    }
    
    public PDF2SVGParserPageDrawer getPDF2SVGParserPageDrawer() {
    	return pdf2svgParserPageDrawer;
    }

}
