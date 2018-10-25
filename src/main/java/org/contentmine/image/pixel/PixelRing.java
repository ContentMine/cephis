package org.contentmine.image.pixel;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
/** a ring of pixels around another ring or point.
 * 
 * @author pm286
 *
 */
public class PixelRing extends PixelList {
	private static final Logger LOG = Logger.getLogger(PixelRing.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public PixelRing() {
	}
	
	public PixelRing(PixelList pixelList) {
		super();
		super.list = pixelList.getList();
	}

	
	public PixelRing getPixelsTouching(PixelRing pixelRing) {
		PixelList touchingPixels = null;
		if (pixelRing != null) {
			touchingPixels = super.getPixelsTouching(pixelRing);
		}
		return new PixelRing(touchingPixels);
	}

	/** grows a new ring "outside" this.
	 * currently developed for nested pixel rings
	 * experimental
	 * 
	 * In principle we could determine the outside by sectioning, but here we assume an onion
	 * ring structure with innerRingList representing the inside
	 * the ouside is simply "not innerRingList" - it need not be whitespace
	 * 
	 * @param innerRingList
	 * @return
	 */
	public PixelRing expandRingOutside(PixelRing innerRing) {
		PixelIsland island = this.getIsland();
		PixelRing newRing = new PixelRing();
		for (Pixel node : this) {
			PixelList pixelList = node.getOrCreateNeighbours(island);
			for (Pixel pixel : pixelList) {
				if (this.contains(pixel)) {
					LOG.trace("skip this");
				} else if (innerRing.contains(pixel)) {
					LOG.trace("skip inner");
				} else {
					LOG.trace("adding "+pixel);
					newRing.add(pixel);
				}
			}
		}
		return newRing;
	}

}
