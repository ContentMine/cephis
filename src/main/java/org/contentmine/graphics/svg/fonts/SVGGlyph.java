package org.contentmine.graphics.svg.fonts;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.eucl.euclid.Vector2;
import org.contentmine.graphics.svg.SVGPath;
import org.contentmine.graphics.svg.path.PathPrimitiveList;

/** holds an outline glyph.
 * will generally hold the SVG primitives that make the glyph
 * and probably information on origins, etc.
 * 
 * @author pm286
 *
 */
public class SVGGlyph extends SVGPath {
	private static final Logger LOG = Logger.getLogger(SVGGlyph.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private Real2 bboxOrigin;

	public SVGGlyph() {
		super();
	}

	public static SVGGlyph createRelativeToBBoxOrigin(PathPrimitiveList pathPrimitiveList) {
		SVGGlyph glyph = null;
		if (pathPrimitiveList != null) {
			glyph = new SVGGlyph();
			glyph.setPathPrimitiveList(pathPrimitiveList);
		}
		return glyph;
	}

	public void setPathPrimitiveList(PathPrimitiveList pathPrimitiveList) {
		this.primitiveList = pathPrimitiveList;
		updatePathSignatureAndOrigin();
	}

	private void updatePathSignatureAndOrigin() {
		getBoundingBox();
		getOrCreateSignatureAttributeValue();
		getOrCreateBBoxOrigin();
		translatePathToBBoxOrigin();
	}

	public void translatePathToBBoxOrigin() {
		getOrCreateBBoxOrigin();
		if (bboxOrigin != null) {
			Real2 r2 = new Real2(bboxOrigin);
			r2.negative();
			Transform2 t2 = new Transform2(new Vector2(r2));
			this.applyTransform(t2);
		}
		
	}

	/** picks the LL corner of the BBox.
	 * may not be the glyph origin if we have descenders
	 * 
	 * @return
	 */
	public Real2 getOrCreateBBoxOrigin() {
		if (bboxOrigin == null) {
			getBoundingBox();
			if (boundingBox != null) {
				Real2[] corners = boundingBox.getLLURCorners();
				bboxOrigin = corners == null ? null : corners[0];
			}
		}
		return bboxOrigin;
	}

	public String toString() {
		String s = bboxOrigin+": "+getOrCreateSignatureAttributeValue();
		return s;
	}
//	public String getOrCreateSignature() {
//		if (this.signature == null) {
////			getOrCreatePath();
//			signature = this.getSignature();
//		}
//		return signature;
//		
//	}

	public String getOrCreateSignature() {
		String signature = getOrCreateSignatureAttributeValue();
		return signature;
	}

//	/** gets the path.
//	 * if null, try to creat from Primitives.
//	 * @return
//	 */
//	public SVGPath getOrCreatePath() {
//		if pathPrimitiveList != null) {
//			this.this = new SVGPath(this.pathPrimitiveList);
//		}
//		return path;
//	}

//	@Override
//	public Real2Range getBoundingBox() {
//		return path == null ? null : path.getBoundingBox();
//	}

}
