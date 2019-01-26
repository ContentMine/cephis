package org.contentmine.image.pixel;

import java.io.File;
import java.util.ArrayList;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Axis.Axis2;
import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.cache.GenericAbstractList;

public class PixelGraphList extends GenericAbstractList<PixelGraph> {
	private static final Logger LOG = Logger.getLogger(PixelGraphList.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public PixelGraphList() {
		init();
	}

	/** deep copy constructor.
	 * 
	 * @param pageCacheList
	 */
	public PixelGraphList(PixelGraphList pixelGraphList) {
		this.genericList = new ArrayList<PixelGraph>(pixelGraphList.genericList);
	}
	
	private void init() {
	}

	public void drawGraphs(File pngFile) {
		SVGG gggg = new SVGG();
		for (PixelGraph graph : this) {
			gggg.appendChild(graph.drawEdgesAndNodes());
		}
		SVGSVG.wrapAndWriteAsSVG(gggg, pngFile);
	}

	public void mergeNodesCloserThan(double dist) {
		for (PixelGraph graph : this) {
			graph.mergeNodesCloserThan(dist);
		}
	}

	public void snapNodesToArray(IntArray xArray, Axis2 axis, int minEdgeCount) {
		for (PixelGraph graph : this) {
			PixelNodeList nodeList = graph.getOrCreateNodeList();
			for (PixelNode node : nodeList) {
				if (node.getEdges().size() > minEdgeCount) {
					LOG.debug("N>1 "+node);
					node.snapToArray(xArray, axis);
				}
			}
		}
	}
	
	

	
}
