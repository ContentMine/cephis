package org.contentmine.image.pixel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGG;

public class PixelRingList implements Iterable<PixelRing> {

	private final Logger LOG = Logger.getLogger(PixelRingList.class);
	
	public static final String[] DEFAULT_COLOURS = {"red", "cyan", "orange", "green", "magenta", "blue"};
	private List<PixelRing> ringList;
	private PixelIsland island;

	private PixelRing outline;
	
	public PixelRingList() {
		init();
	}

	private void init() {
		ringList = new ArrayList<PixelRing>();
	}
	
	public PixelRingList(Collection<PixelRing> pixelCollection) {
		init();
		ringList.addAll(pixelCollection);
	}

	public Iterator<PixelRing> iterator() {
		return ringList.iterator();
	}
	
	public PixelRing get(int i) {
		return (ringList == null || i < 0 || i >= ringList.size()) ? null : ringList.get(i);
	}
	
	public void add(PixelRing pixelRing) {
		if (ringList == null) {
			init();
		}
		ringList.add(pixelRing);
	}
	
	public List<PixelRing> getList() {
		return ringList;
	}

	public int size() {
		return ringList == null ? 0 : ringList.size();
	}

	public void addAll(PixelRingList RingList) {
		this.ringList.addAll(RingList.getList());
	}

	public boolean contains(PixelList pixelList) {
		return ringList != null && ringList.contains(pixelList);
	}

	public boolean remove(PixelList pixelList) {
		if (ringList != null) {
			return ringList.remove(pixelList);
		}
		return false;
	}

	/** plots rings in different colours
	 * 
	 * cycles through the colours if not enough.
	 * 
	 * @param gg SVGG to which everything is drawn; if null create one
	 * @param fill list of colours; if null, defaults are used
	 */
	public SVGG plotPixels(SVGG gg, String[] fill) {
		if (gg == null) {
			gg = new SVGG();
		}
		if (fill == null) {
			fill = DEFAULT_COLOURS;
		}
		int i = 0;
		
		for (PixelRing pixelRing : this) {
			SVGG g = pixelRing.plotPixels(fill[i]);
			gg.appendChild(g);
			i = (i + 1) % fill.length;
		}
		return gg;
	}
	
	/** plot rings with defaults
	 * 
	 */
	public SVGG plotPixels() {
		return plotPixels(null, null);
	}

	/** remove islands with only a few pixels (likely to be artefacts)
	 * 
	 * @param size max size of islands to remove
	 * 
	 */
	public void removeMinorIslands(int size) {
		for (int ring = 0; ring < ringList.size(); ring++) {
			PixelRing pixelRing = ringList.get(ring);
			// make copy of ring as island to isolate the ring
			PixelIsland newIsland = PixelIsland.createSeparateIslandWithClonedPixels(pixelRing, true);
			int oldSize = newIsland.size();
			newIsland.removeMinorIslands(size);
			int newSize = newIsland.size();
			// if it's changed swap the old pixels for the new
			if (newSize != oldSize) {
				ringList.set(ring, new PixelRing(newIsland.getPixelList()));
				LOG.trace("island size after "+newIsland.size());
			}
		}
	}

	public void setIsland(PixelIsland pixelIsland) {
		this.island = pixelIsland;
	}

	public void plotRings(SVGG g, String[] colours) {
		for (int i = 0; i < size(); i++) {
			get(i).plotPixels(g, colours[i % colours.length]);
		}
	}

	public SVGG getGraphEdges(PixelIsland island, String[] fill) {
		SVGG g = new SVGG();
		PixelList outline;
		if (size() > 1) {
			outline = get(0).getPixelsWithOrthogonalContactsTo(get(1), island);
			outline.plotPixels(g, fill[0]);
			PixelIsland outlineIsland = PixelIsland.createSeparateIslandWithClonedPixels(outline, true);
			PixelGraph graph = PixelGraph.createGraph(outlineIsland);
//			PixelNodeList nodeList = graph.getNodeList();
			PixelEdgeList edgeList = graph.getOrCreateEdgeList();
			for (PixelEdge edge : edgeList) {
				PixelSegmentList segmentList = edge.getOrCreateSegmentList(2);
				g.appendChild(segmentList.getOrCreateSVG());
			}
		}
		return g;
	}

	public PixelRing getOrCreateOutline() {
		if (outline == null) {
			if (size() > 1) {
				outline = get(1).getPixelsTouching(get(0));
			}
		}
		return outline;
	}
	
	public SVGG plotOutline(String colour) {
		SVGG g = null;
		outline = getOrCreateOutline();
		outline.plotPixels(g, colour);
		return g;
	}

	public PixelRing getOuterPixelRing() {
		return get(0);
	}
	
	public PixelRing getRing(int i) {
		PixelRing ring1 = get(i);
		PixelRing ring0 = get(i-1);
		PixelRing ring = ring0 == null ? ring1 : ring1.getPixelsTouching(ring0);
		return ring;
	}

}
