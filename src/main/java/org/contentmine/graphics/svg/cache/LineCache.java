package org.contentmine.graphics.svg.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.euclid.RealRange.Direction;
import org.contentmine.eucl.euclid.util.MultisetUtil;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGLineList;
import org.contentmine.graphics.svg.SVGPolyline;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGLine.LineDirection;
import org.contentmine.graphics.svg.linestuff.AxialLineList;
import org.contentmine.graphics.svg.linestuff.HorizontalLineComparator;
import org.contentmine.graphics.svg.plot.AbstractPlotBox;
import org.contentmine.graphics.svg.plot.AnnotatedAxis;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/** extracts texts within graphic area.
 * 
 * @author pm286
 *
 */
public class LineCache extends AbstractCache {
	private static final String BLACK = "black";
	static final Logger LOG = Logger.getLogger(LineCache.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	private static Double STROKE_WIDTH_FACTOR = 5.0;

	private List<SVGLine> horizontalLines;
	private List<SVGLine> verticalLines;

	private List<SVGLine> lineList;
	private SVGLineList longHorizontalLineList;
	// where possible short horizontal lines will be contained within sibling tuples
	private SVGLineList shortHorizontalLineList;
	private List<SVGLineList> horizontalSiblingsList;
	private SVGLineList topHorizontalLineList;
	private SVGLineList bottomHorizontalLineList;
	private Multiset<Double> horizontalLineStrokeWidthSet;
	private List<SVGLine> allLines;

	private AxialLineList longHorizontalEdgeLines;
	private AxialLineList longVerticalEdgeLines;
	private SVGRect fullLineBox;
	private Real2Range lineBbox;

	// parameters 
	private Double axialLinePadding = 10.0; // to start with
	private Double cornerEps = 0.5; // to start with
	private Double lineEps = 5.0; // to start with
	// for display only
	private double joinEps = 1.0; // tolerance for joining lines 
	public LineCache(ComponentCache containingComponentCache) {
		super(containingComponentCache);
		siblingShapeCache = containingComponentCache.getOrCreateShapeCache();
		lineList =  siblingShapeCache == null ? new ArrayList<SVGLine>() : siblingShapeCache.getLineList();
		init();
	}
	
	private void init() {
		// leave lists as null untill needed
	}

	/** the bounding box of the actual line components
	 * The extent of the context (e.g. svgCache) may be larger
	 * @return the bounding box of the contained lines
	 */
	public Real2Range getBoundingBox() {
		return getOrCreateBoundingBox(lineList);
	}

	public List<? extends SVGElement> getOrCreateElementList() {
		return lineList;
	}

	public List<SVGLine> getOrCreateLineList() {
		if (lineList == null) {
			
		}
		return lineList;
	}
	
	public void makeLongHorizontalAndVerticalEdges() {
		
		if (lineList != null && lineList.size() > 0) {
			lineBbox = SVGElement.createBoundingBox(lineList);
			getOrCreateLongHorizontalEdgeLines();
			getOrCreateLongVerticalEdgeLines();
		}
		return;
	}

	private void getOrCreateLongVerticalEdgeLines() {
		if (longVerticalEdgeLines == null) {
			longVerticalEdgeLines = getSortedLinesCloseToEdge(verticalLines, LineDirection.VERTICAL, lineBbox);
		}
	}

	private void getOrCreateLongHorizontalEdgeLines() {
		if (longHorizontalEdgeLines == null) {
			longHorizontalEdgeLines = getSortedLinesCloseToEdge(horizontalLines, LineDirection.HORIZONTAL, lineBbox);
		}
	}

	public void makeFullLineBoxAndRanges() {
		
		fullLineBox = null;
		RealRange fullboxXRange = null;
		RealRange fullboxYRange = null;
		if (longHorizontalEdgeLines != null && longHorizontalEdgeLines.size() > 0) {
			fullboxXRange = createRange(longHorizontalEdgeLines, Direction.HORIZONTAL);
			fullboxXRange = fullboxXRange == null ? null : fullboxXRange.format(AbstractPlotBox.FORMAT_NDEC);
		}
		if (longVerticalEdgeLines != null && longVerticalEdgeLines.size() > 0) {
			fullboxYRange = createRange(longVerticalEdgeLines, Direction.VERTICAL);
			fullboxYRange = fullboxYRange == null ? null : fullboxYRange.format(AbstractPlotBox.FORMAT_NDEC);
		}
		if (fullboxXRange != null && fullboxYRange != null) {
			fullLineBox = SVGRect.createFromRealRanges(fullboxXRange, fullboxYRange);
			fullLineBox.format(AbstractPlotBox.FORMAT_NDEC);
		}
		if (fullLineBox == null) {
			Real2Range pathBox = ownerComponentCache.getOrCreatePathCache().getBoundingBox();
			// not sure why we are comparing to pathBox; may be null
			for (SVGRect rect : ownerComponentCache.getOrCreateShapeCache().getRectList()) {
				Real2Range rectRange = rect.getBoundingBox();
				if (rectRange == null) {
					throw new RuntimeException("null range: "+rect.toXML());
				}
				if (pathBox != null && pathBox.isEqualTo(rectRange, axialLinePadding)) {
					fullLineBox = rect;
					break;
				}
			}
		}
	}

	public SVGLineList getTopHorizontalLineList() {
		topHorizontalLineList = new SVGLineList();
		getOrCreateLongHorizontalLineList();
		for (SVGLine line : longHorizontalLineList) {
			if (Real.isEqual(ownerComponentCache.getBoundingBox().getYMin(), line.getMidPoint().getY(), lineEps )) {
				topHorizontalLineList.add(line);
			}
		}
		return topHorizontalLineList;
	}

	public SVGLineList getBottomHorizontalLineList() {
		bottomHorizontalLineList = new SVGLineList();
		getOrCreateLongHorizontalLineList();
		for (SVGLine line : longHorizontalLineList) {
			if (Real.isEqual(ownerComponentCache.getBoundingBox().getYMax(), line.getMidPoint().getY(), lineEps )) {
				bottomHorizontalLineList.add(line);
			}
		}
		return bottomHorizontalLineList;
	}

	/** get lines which are "almost" the same length as the width of the owner cache bbox.
	 * 
	 * @return
	 */
	public SVGLineList getOrCreateLongHorizontalLineList() {
		if (longHorizontalLineList == null) {
			longHorizontalLineList = new SVGLineList();
			getOrCreateHorizontalLineList();
			Real2Range ownerBBox = getOrCreateComponentCacheBoundingBox();
			if (ownerBBox == null) {
				throw new RuntimeException("null ownerBBox");
			}
			RealRange xrange = ownerBBox.getRealRange(Direction.HORIZONTAL);
			for (SVGLine line : horizontalLines) {
				RealRange lineRange = line.getRealRange(Direction.HORIZONTAL);
				if (RealRange.isEqual(xrange, lineRange, lineEps )) {
					longHorizontalLineList.add(line);
				}
			}
		}
		LOG.trace("poly2a "+ownerComponentCache.shapeCache.getPolylineList());
		return longHorizontalLineList;
	}

	public SVGLineList getOrCreateShortHorizontalLineList() {
		if (shortHorizontalLineList == null) {
			shortHorizontalLineList = new SVGLineList();
			getOrCreateHorizontalLineList();
			RealRange xrange = getOrCreateComponentCacheBoundingBox().getRealRange(Direction.HORIZONTAL);
			for (SVGLine line : horizontalLines) {
				if (line.getLength() < xrange.getRange() - lineEps ) {
					shortHorizontalLineList.add(line);
				}
			}
			transferShortHorizontalLinesToSiblingLineLists();
			
		}
		return shortHorizontalLineList;
	}

	private void transferShortHorizontalLinesToSiblingLineLists() {
		double lastY = -Double.MAX_VALUE;
		horizontalSiblingsList = new ArrayList<SVGLineList>();
		SVGLineList horizontalSiblings = null;
		for (int i = shortHorizontalLineList.size() - 1; i >= 0; i--) {
			SVGLine shortHorizontal = shortHorizontalLineList.get(i);
			double y = shortHorizontal.getMidPoint().getY();
			if (!Real.isEqual(lastY, y)) {
				horizontalSiblings = new SVGLineList();
				horizontalSiblingsList.add(horizontalSiblings);
			}
			lastY = y;
			horizontalSiblings.add(shortHorizontal);
			shortHorizontalLineList.remove(i);
		}
		Collections.reverse(horizontalSiblingsList);
	}

	public Multiset<Double> getHorizontalLineStrokeWidthSet() {
		if (horizontalLineStrokeWidthSet == null) {
			horizontalLineStrokeWidthSet = HashMultiset.create();
			getOrCreateHorizontalLineList();
			for (SVGLine horizontalLine : horizontalLines) {
				Double strokeWidth = horizontalLine.getStrokeWidth(); 
				if (strokeWidth == null) strokeWidth = 0.0;
				horizontalLineStrokeWidthSet.add(strokeWidth);
			}
		}
		return horizontalLineStrokeWidthSet;
	}

	/** creates horizntal and vertical lines
	 * splits "L"-shaped polylines into two lines
	 * this may or may not be a good idea.
	 * 
	 * @param originalSvgElement modified
	 */
	public void createHorizontalAndVerticalLines() {
		
		getOrCreateHorizontalLineList();
		getOrCreateVerticalLineList();
		List<SVGPolyline> polylineList = siblingShapeCache.getPolylineList();
		List<SVGPolyline> axialLShapes = SVGPolyline.findLShapes(polylineList);
		for (int i = axialLShapes.size() - 1; i >= 0; i--) {
			removeLShapesAndReplaceByLines(polylineList, axialLShapes.get(i), ownerComponentCache.getInputSVGElement());
		}
		allLines = new ArrayList<SVGLine>();
		allLines.addAll(this.horizontalLines);
		allLines.addAll(this.verticalLines);
	}

	public List<SVGLine> getOrCreateVerticalLineList() {
		if (verticalLines == null) {
			verticalLines = SVGLine.findHorizontalOrVerticalLines(lineList, LineDirection.VERTICAL, AnnotatedAxis.EPS);
			verticalLines = SVGLine.mergeParallelLines(verticalLines, joinEps);
		}
		return verticalLines;
	}

	public List<SVGLine> getOrCreateHorizontalLineList() {
		if (horizontalLines == null) {
			List<SVGLine> horizontalLines0 = SVGLine.findHorizontalOrVerticalLines(lineList, LineDirection.HORIZONTAL, AnnotatedAxis.EPS);
			horizontalLines = SVGLine.mergeParallelLines(horizontalLines0, joinEps );
			if (horizontalLines0.size() != horizontalLines.size()) {
				LOG.info("merged horizontal lines: ");
			}
			Collections.sort(horizontalLines, new HorizontalLineComparator());
			getOrCreateLongHorizontalLineList();
			getOrCreateShortHorizontalLineList();
		}
		return horizontalLines;
	}

	public RealRange createRange(SVGLineList lines, Direction direction) {
		RealRange hRange = null;
		if (lines.size() > 0) {
			SVGLine line0 = lines.get(0);
			hRange = line0.getReal2Range().getRealRange(direction);
			SVGLine line1 = lines.get(1);
			if (line1 != null && !line1.getReal2Range().getRealRange(direction).isEqualTo(hRange, cornerEps)) {
				hRange = null;
	//				throw new RuntimeException("Cannot make box from HLines: "+line0+"; "+line1);
			}
		}
		return hRange;
	}

	void removeLShapesAndReplaceByLines(List<SVGPolyline> polylineList, SVGPolyline axialLShape, AbstractCMElement svgElement) {
		LOG.trace("replacing LShapes by splitLines");
		
		addNewHVLine(axialLShape, svgElement, verticalLines, 1);
		addNewHVLine(axialLShape, svgElement, horizontalLines, 0);
		
		polylineList.remove(axialLShape);
		axialLShape.detach();
	}

	private void addNewHVLine(SVGPolyline axialLShape, AbstractCMElement svgElement, List<SVGLine> hvLineList, int axis) {
		SVGLine hvLine = axialLShape.getLineList().get(axis);
		hvLine.setCSSStyle(axialLShape.getStyle());
		svgElement.appendChild(hvLine);
		hvLineList.add(hvLine);
		this.lineList.add(hvLine);
	}

//	private void addNewVLine(SVGPolyline axialLShape, SVGElement svgElement, List<SVGLine> hvLineList, int axis) {
//		SVGLine vLine = axialLShape.getLineList().get(axis);
//		vLine.setCSSStyle(axialLShape.getStyle());
//		svgElement.appendChild(vLine);
//		hvLineList.add(vLine);
//		this.lineList.add(vLine);
//	}

	public AxialLineList getSortedLinesCloseToEdge(List<SVGLine> lines, LineDirection direction, Real2Range bbox) {
		RealRange.Direction rangeDirection = direction.isHorizontal() ? RealRange.Direction.HORIZONTAL : RealRange.Direction.VERTICAL;
		RealRange parallelRange = direction.isHorizontal() ? bbox.getXRange() : bbox.getYRange();
		RealRange perpendicularRange = direction.isHorizontal() ? bbox.getYRange() : bbox.getXRange();
		AxialLineList axialLineList = new AxialLineList(direction);
		for (SVGLine line : lines) {
			Real2 xy = line.getXY(0);
			Double perpendicularCoord = direction.isHorizontal() ? xy.getY() : xy.getX();
			RealRange lineRange = line.getRealRange(rangeDirection);
			if (lineRange.isEqualTo(parallelRange, axialLinePadding)) {
				if (isCloseToBoxEdge(perpendicularRange, perpendicularCoord, axialLinePadding)) {
					axialLineList.add(line);
					line.normalizeDirection(AnnotatedAxis.EPS);
				}
			}
		}
		axialLineList.sort();
		return axialLineList;
	}


	private static boolean isCloseToBoxEdge(RealRange parallelRange, Double parallelCoord, Double axialLinePadding) {
		return Real.isEqual(parallelCoord, parallelRange.getMin(), axialLinePadding) ||
				Real.isEqual(parallelCoord, parallelRange.getMax(), axialLinePadding);
	}

	public SVGRect getFullLineBox() {
		return fullLineBox;
	}

	
	public AbstractCMElement createColoredHorizontalLineStyles() {
		return createColoredHorizontalLineStyles(ComponentCache.MAJOR_COLORS);
	}

	/** we are now emphasing lines by width and blackness, not color, 
	 * so this arg is 
	 * @param color
	 * @return
	 */
	public AbstractCMElement createColoredHorizontalLineStyles(String[] color) {
		List<SVGLine> lines = getOrCreateHorizontalLineList();
		Multiset<Double> lineWidths = getHorizontalLineStrokeWidthSet();
		AbstractCMElement g = new SVGG();
		if (lineWidths != null) {
			List<Multiset.Entry<Double>> sortedLineWidths = MultisetUtil.createListSortedByCount(lineWidths);
			for (SVGLine line : lines) {
				Double strokeWidth = line.getStrokeWidth();
				for (int i = 0; i < sortedLineWidths.size(); i++) {
					Multiset.Entry<Double> entry = sortedLineWidths.get(i);
					if (entry.getElement().equals(strokeWidth)) {
						SVGLine line1 = (SVGLine) line.copy();
						line1.setStrokeWidth(strokeWidth * STROKE_WIDTH_FACTOR );
						line1.setStroke(BLACK);
						line1.addTitle(""+strokeWidth);
						g.appendChild(line1);
						break;
					}
				}
			}
		}
		return g;
	}

	@Override
	public String toString() {
		String s = ""
			+ "hor: "+horizontalLines.size()+"; "
			+ "vert: "+verticalLines.size()+"; "
			+ "line: "+lineList.size()+"; "
			+ "longH: "+longHorizontalLineList.size()+"; "
			+ "shortH: "+shortHorizontalLineList.size()+"; "
			+ "siblingsH: "+horizontalSiblingsList.size()+"; ";
		return s;

	}

	public List<SVGLineList> getHorizontalSiblingsList() {
		return horizontalSiblingsList;
	}

	public void createSpecializedLines() {
		createHorizontalAndVerticalLines();
		makeLongHorizontalAndVerticalEdges();
		makeFullLineBoxAndRanges();
	}

	@Override
	public void clearAll() {
		superClearAll();
		horizontalLines = null;
		verticalLines = null;

		lineList = null;
		longHorizontalLineList = null;
		shortHorizontalLineList = null;
		horizontalSiblingsList = null;
		topHorizontalLineList = null;
		bottomHorizontalLineList = null;
		horizontalLineStrokeWidthSet = null;
		allLines = null;

		longHorizontalEdgeLines = null;
		longVerticalEdgeLines = null;
		fullLineBox = null;
		lineBbox = null;
	}

	public void add(SVGLine line) {
		getOrCreateLineList().add(line);
	}

	public void addLines(List<SVGLine> lineList) {
		getOrCreateLineList().addAll(lineList);
	}



	

}
