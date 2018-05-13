package org.contentmine.graphics.svg;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.graphics.AbstractCMElement;

public class SVGLineList extends SVGG implements Iterable<SVGLine> {
	
	public enum SiblingType {
		HORIZONTAL_SIBLINGS, // with common Y
		VERTICAL_SIBLINGS, // with common X
	}
	
	private static Logger LOG = Logger.getLogger(SVGLineList.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private double siblingEps = 0.2; // difference in common coordinate
	protected ArrayList<SVGLine> lineList;
	private SiblingType type;

	public SVGLineList() {
		super();
	}
	
	public SVGLineList(List<SVGLine> lines) {
		this.lineList = new ArrayList<SVGLine>(lines);
	}

	/** adds all SVGLines in collection to new SVGLineList
	 * 
	 * @param elements List which potentially contains SVGLine elements
	 * @return empty list if no lines
	 */
	public static SVGLineList createLineList(List<SVGElement> elements) {
		SVGLineList lineList = new SVGLineList();
		for (AbstractCMElement element : elements) {
			if (element instanceof SVGLine) {
				lineList.add((SVGLine) element);
			}
		}
		return lineList;
	}

	public void setType(SiblingType type) {
		ensureLines();
		if (checkLines(type)) {
			this.type = type;
		}
	}
	
	/** maybe create a SiblingLines class.
	 * 
	 * @param type
	 * @return
	 */
	public boolean checkLines(SiblingType type) {
		if (lineList == null || lineList.size() == 0) return false;
		Double commonCoord = null;
		for (SVGLine line : lineList) {
			Double coord = null;
			if (type.equals(SiblingType.HORIZONTAL_SIBLINGS)) {
				if (!line.isHorizontal(siblingEps)) {
					throw new RuntimeException("Lines do not obey type: "+type);
				}
				coord = line.getMidPoint().getY();
			} else if (type.equals(SiblingType.VERTICAL_SIBLINGS)) {
				if (! line.isVertical(siblingEps)) {
					throw new RuntimeException("Lines do not obey type: "+type);
				}
			}
			coord = line.getMidPoint().getY();
			if (commonCoord == null) {
				commonCoord = coord;
			} else if (!Real.isEqual(commonCoord, coord, siblingEps)) {
				return false;
			}
		}
		return true;
	}

	public List<SVGLine> getLineList() {
		ensureLines();
		return lineList;
	}

	public Iterator<SVGLine> iterator() {
		ensureLines();
		return lineList.iterator();
	}

	protected void ensureLines() {
		if (lineList == null) {
			lineList = new ArrayList<SVGLine>();
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		ensureLines();
		sb.append(lineList.toString());
		return sb.toString();
	}

	public SVGLine get(int i) {
		ensureLines();
		return (i < 0 || i >= lineList.size()) ? null : lineList.get(i);
	}

	public int size() {
		ensureLines();
		return lineList.size();
	}

	public SVGShape remove(int i) {
		ensureLines();
		if (get(i) != null) {
			return lineList.remove(i);
		}
		return null;
	}
	
	public boolean add(SVGLine line) {
		ensureLines();
		return lineList.add(line);
	}

	public Real2Range getBoundingBox() {
		Real2Range bbox = null;
		for (SVGLine line : lineList) {
			Real2Range bbox0 = line.getBoundingBox();
			if (bbox == null) {
				bbox = bbox0;
			} else {
				bbox = bbox.plus(bbox0);
			}
		}
		return bbox;
	}
}
