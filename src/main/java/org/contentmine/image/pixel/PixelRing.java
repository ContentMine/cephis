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

//	private PixelList pixelList;
	
	public PixelRing() {
	}
	
	public PixelRing(PixelList pixelList) {
		super();
		super.list = pixelList.getList();
	}

//	public PixelList getOrCreatePixelList() {
//		if (pixelList == null) {
//			this.pixelList = new PixelList();
//		}
//		return pixelList;
//	}
	
//	@Override
//	public Iterator<Pixel> iterator() {
//		return pixelList.iterator();
//	}

//	public boolean contains(Pixel pixel) {
//		return getOrCreatePixelList().contains(pixel);
//	}

	public PixelRing getPixelsTouching(PixelRing pixelRing) {
		PixelList touchingPixels = null;
		if (pixelRing != null) {
			touchingPixels = super.getPixelsTouching(pixelRing);
		}
		return new PixelRing(touchingPixels);
	}

//	public SVGG getOrCreateSVG() {
//		return getList().getOrCreateSVG();
//	}

//	public int size() {
//		return getOrCreatePixelList().size();
//	}

//	public void plotPixels(SVGG g, String fill) {
//		this.getOrCreatePixelList().plotPixels(g, fill);
//	}


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

//	public void add(Pixel pixel) {
//		this.getOrCreatePixelList().add(pixel);
//	}


}
