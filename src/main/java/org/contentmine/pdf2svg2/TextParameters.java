package org.contentmine.pdf2svg2;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDFontDescriptor;
import org.apache.pdfbox.util.Matrix;
import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.RealSquareMatrix;
import org.contentmine.eucl.euclid.Transform2;

public class TextParameters {
	private static final Logger LOG = Logger.getLogger(TextParameters.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private Matrix matrix;
	private PDFont font;
	private Transform2 transform2;
	private Angle angle;

	public TextParameters(Matrix matrix, PDFont font) {
		if (matrix == null) {
			throw new RuntimeException("null matrix");
		}
		this.matrix = matrix;
		this.font = font;
		double[][] array = matrix.getValuesAsDouble();
		RealSquareMatrix rsm = new RealSquareMatrix(array);
		transform2 = new Transform2(rsm);
		angle = transform2.getAngleOfRotation();
		LOG.trace("angle "+angle);
		if (font == null) {
			throw new RuntimeException("null font");
		}
    	LOG.trace(matrix.getScaleX()+
    			"/"+matrix.getScaleY()+
    			"/"+matrix.getScalingFactorX()+
    			"/"+matrix.getScalingFactorY()+
    			"//"+matrix.getTranslateX()+
    			"/"+matrix.getTranslateY()+
    			""
    			);
    	
    	 //Font/TrueType/KAJWHP+Helvetica/org.apache.pdfbox.pdmodel.font.PDFontDescriptor@26aa12dd/[0.001,0.0,0.0,0.001,0.0,0.0]
    			 
    	PDFontDescriptor fdesc = font.getFontDescriptor();
    	// more later
		LOG.trace("fw "+font.getAverageFontWidth()+   // 472.5 
    			"/sw "+font.getSpaceWidth()+            // 633.78906
    			"/ty "+font.getType()+                  // Font
    			"/st "+font.getSubType()+               // TrueType
    			"/nm "+font.getName()+                  // KAJWHP+Helvetica
    			"/"+fdesc+        // object
    			"{"+
    			    "ff: "+fdesc.getFontFamily()+";"+
    			    "wt: "+fdesc.getFontWeight()+";"+
    			    "it: "+fdesc.isItalic()+";"+
    			    "sy: "+fdesc.isSymbolic()+";"+
    			"}"+
    			"/"+font.getFontMatrix()+            // /[0.001,0.0,0.0,0.001,0.0,0.0]  3*2 ??
    			"");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((font == null) ? 0 : font.hashCode());
		result = prime * result + ((matrix == null) ? 0 : matrix.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TextParameters other = (TextParameters) obj;
		if (font == null) {
			if (other.font != null)
				return false;
		} else if (!font.equals(other.font))
			return false;
		if (matrix == null) {
			if (other.matrix != null)
				return false;
		} else if (!matrix.equals(other.matrix))
			return false;
		return true;
	}
	
	public boolean hasEqualFont(TextParameters textParameters) {
		if (textParameters == null) return false;
		PDFont tFont = textParameters.font;
		return this.font.equals(tFont);
	}

	public Matrix getMatrix() {
		return matrix;
	}

	public PDFont getFont() {
		return font;
	}

	public Real2 getScales() {
		return new Real2(matrix.getScaleX(), matrix.getScaleY());
	}

	public Double getFontSize() {
		return new Double(matrix.getScaleX());
	}

	public Transform2 getTransform2() {
		return transform2;
	}

	public Angle getAngle() {
		return angle;
	}

	
}
