package org.contentmine.graphics.svg.plot.forest;

import java.io.File;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.RangeScaler;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGPoly;
import org.contentmine.graphics.svg.SVGPolygon;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.cache.ComponentCache;
import org.contentmine.graphics.svg.cache.LineCache;
import org.contentmine.graphics.svg.cache.PathCache;
import org.contentmine.graphics.svg.cache.PolygonCache;
import org.contentmine.graphics.svg.cache.PolylineCache;
import org.contentmine.graphics.svg.cache.RectCache;
import org.contentmine.graphics.svg.plot.XPlotBox;

/** reads vector/text SVG and creates a Forest Plot.
 * 
 * @author pm286
 *
 */
public class ForestPlot {

	private static final Logger LOG = Logger.getLogger(ForestPlot.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	private static final double PERCENT_100 = 100.;
	private static final String NO_SCALE_FILL = "red";
	private static final String SCALE_FILL = "purple";
	
	private static final String SVG = ".svg";
	private static final String VERTICAL = "vertical";
	private static final String NONAXIAL = "nonaxial";
	private static final String POLYLIST = "polylist";
	private static final String LINES = "lines";
	private static final String RECTS = "rects";
	private static final String CACHE = "cache";
	private static final String POLYGONS = "polygons";
	private static final String DEFAULT_LINE_FILL = "fill:blue;";
	
	private static final String DEFAULT_WEIGHT_2_STYLE = "font-size:5.0;fill:blue;font-weight:bold;font-family:helvetica;";
	private static final Real2 WEIGHT_OFFSET_UP = new Real2(0.0, -2.0);
	private static final String DEFAULT_WEIGHT_1_STYLE = "font-size:5.0;fill:green;font-weight:bold;font-family:helvetica;";

	private RangeScaler rangeScaler;
	private double totalRectArea;
	private boolean applyScale = true;
	private boolean logarithmicScale;
	private File outputDir;
	private double diamondEps;
	private String fileRoot;
	
	public ForestPlot() {
		setDefaults();
	}

	private void setDefaults() {
		outputDir = new File("target/forest");
		setDiamondEps(0.5);
	}

	public ComponentCache createCaches(AbstractCMElement svgElement) {
		XPlotBox xPlotBox = new XPlotBox();
		ComponentCache componentCache = new ComponentCache(xPlotBox); 
		componentCache.readGraphicsComponentsAndMakeCaches(svgElement);
	//		List<SVGLine> lineList = componentCache.getOrCreateLineCache().getOrCreateLineList();
	//		List<SVGText> textList = componentCache.getOrCreateTextCache().getTextList();
		return componentCache;
	}

	private double extractTransformX(Real2 xy) {
		double x = xy.getX();
		if (applyScale) {
			x = rangeScaler.transformInputToOutput(x);
			x = Math.pow(10.0, x);
		} else {
			x = (int) x;
		}
		return x;
	}

	private SVGG plotLineEnd(SVGLine line, int end) {
		SVGG g = new SVGG();
		Real2 xy = line.getXY(end);
		double x = extractTransformX(xy);
		Real2 offset = applyScale ? WEIGHT_OFFSET_UP : new Real2(0.0, 5.0);
		SVGText text = SVGText.createDefaultText(xy.plus(offset), String.valueOf(Util.format(x, 2)));
		text.setFontSize(5.0);
		text.setFill(applyScale ? SCALE_FILL : NO_SCALE_FILL);
		text.setFontWeight("bold");
		text.setFontFamily("helvetica");
		
		g.appendChild(text);
		
		return g;
	}

	private SVGG plotRectCentre(SVGRect rect) {
		SVGG g = new SVGG();
		Real2Range bbox = rect.getBoundingBox();
		Real2 xy = bbox.getCentroid();
		double x = Util.format(extractTransformX(xy), 2);
		SVGText text = SVGText.createDefaultText(xy.plus(WEIGHT_OFFSET_UP), String.valueOf(x));
		String style = DEFAULT_WEIGHT_2_STYLE;
		text.setCSSStyle(style);
		g.appendChild(text);
		double area = PERCENT_100 * bbox.calculateArea();
		area /= totalRectArea;
		area *= 2; // because there are two rects for each one displayed???
		SVGText text1 = SVGText.createDefaultText(xy.plus(new Real2(5.0, 5.0)), ""+Util.format(area, 2));
		text1.setFontSize(5.0);
		String weightStyle  = DEFAULT_WEIGHT_1_STYLE;
		text1.setCSSStyle(weightStyle);
		g.appendChild(text1);
		return g;
	}

	private List<SVGPolygon> createPolygons(List<SVGLine> lineList) {
		List<SVGPolygon> polygonList = SVGPolygon.createPolygonsFromLines(lineList, diamondEps);
		return polygonList;
	}

	public void readCacheAndAnalyze(File inputDir, String fileRoot) {
		this.fileRoot = fileRoot;
		rangeScaler = new RangeScaler();
		rangeScaler.setInputRange(new RealRange(235, 364));
		rangeScaler.setOutputRange(new RealRange(-1.0,1.0)); // logarithmic
		File infile = new File(inputDir, fileRoot + SVG);
		AbstractCMElement svgElement = SVGElement.readAndCreateSVG(infile);
		ComponentCache componentCache = createCaches(svgElement);
		PathCache pathCache = componentCache.getOrCreatePathCache();
		LineCache lineCache = componentCache.getOrCreateLineCache();
		List<SVGLine> lines = lineCache.getOrCreateLineList();
		List<SVGLine> horizontalLines = lineCache.getOrCreateHorizontalLineList();
		List<SVGLine> verticalLines = lineCache.getOrCreateVerticalLineList();
		SVGSVG.wrapAndWriteAsSVG(verticalLines, new File(outputDir, fileRoot+"/"+VERTICAL+SVG));
		List<SVGLine> nonAxialLineList = lines;
		processNonAxialLines(fileRoot, horizontalLines, verticalLines, nonAxialLineList);
		
		SVGG gg = new SVGG();
		processLines(fileRoot, horizontalLines, gg);		
		
		processRects(fileRoot, componentCache, gg);
		PolylineCache polylineCache = componentCache.getOrCreatePolylineCache();
		PolygonCache polygonCache = componentCache.getOrCreatePolygonCache();
		SVGG g = new SVGG();
		List<SVGPolygon> polygonList = polygonCache.getOrCreatePolygonList();
		List<SVGPolygon> diamondPolygons = createPolygons(nonAxialLineList);
		polygonList.addAll(diamondPolygons);
		for (SVGPolygon polygon : polygonList) {
			if (polygon.size() == 4) {
				String style = "fill:red;stroke:green;stroke-width:3.0;";
				polygon.setCSSStyle(style);
				g.appendChild(polygon);
			}
		}
		SVGSVG.wrapAndWriteAsSVG(g, new File(outputDir, fileRoot+"/"+POLYGONS+SVG));
		SVGElement svgg = componentCache.getOrCreateConvertedSVGElement();
		SVGSVG.wrapAndWriteAsSVG(svgg, new File(outputDir, fileRoot+"/"+CACHE+SVG));
	}

	private void processNonAxialLines(String fileRoot, List<SVGLine> horizontalLines, List<SVGLine> verticalLines,
			List<SVGLine> nonAxialLineList) {
		nonAxialLineList.removeAll(horizontalLines);
		nonAxialLineList.removeAll(verticalLines);
		for (int i = nonAxialLineList.size() - 1; i >= 0; i--) {
			SVGLine svgLine = nonAxialLineList.get(i);
			if (nonAxialLineList.get(i).isHorizontal(0.1) || svgLine.isVertical(0.1)) {
				nonAxialLineList.remove(i);
			}
		}
		// create diamonds from lines
		List<SVGPoly> polyList = SVGPoly.createSVGPolyList(nonAxialLineList, diamondEps);
		LOG.debug("polyList: "+polyList);

		SVGSVG.wrapAndWriteAsSVG(polyList, new File(outputDir, fileRoot+"/"+POLYLIST+SVG));
	}

	private void processLines(String fileRoot, List<SVGLine> horizontalLines, SVGG gg) {
		for (SVGLine line : horizontalLines) {
			String lineStyle = DEFAULT_LINE_FILL;
			line.setCSSStyle(lineStyle);
			gg.appendChild(line);
			gg.appendChild(plotLineEnd(line, 0));
			gg.appendChild(plotLineEnd(line, 1));
			Real2Range bbox0 = line.getBoundingBox();
		}
		SVGSVG.wrapAndWriteAsSVG(gg, new File(outputDir, fileRoot+"/"+LINES+SVG));
	}

	private void processRects(String fileRoot, ComponentCache componentCache, SVGG gg) {
		RectCache rectCache = componentCache.getOrCreateRectCache();
		List<SVGRect> rectList = rectCache.getOrCreateRectList();
		totalRectArea = 0.0;
		for (SVGRect rect : rectList) {
			totalRectArea += rect.getBoundingBox().calculateArea();
		}
		for (SVGRect rect : rectList) {
			rect.setFill("blue");
			gg.appendChild(rect);
			gg.appendChild(plotRectCentre(rect));
//			Real2Range bbox0 = rect.getBoundingBox();
		}
		SVGSVG.wrapAndWriteAsSVG(gg, new File(outputDir, fileRoot+"/"+RECTS+SVG));
	}
	
	public boolean isLogarithmicScale() {
		return logarithmicScale;
	}

	public void setLogarithmicScale(boolean logarithmicScale) {
		this.logarithmicScale = logarithmicScale;
	}

	public boolean isApplyScale() {
		return applyScale;
	}

	public void setApplyScale(boolean applyScale) {
		this.applyScale = applyScale;
	}

	public double getDiamondEps() {
		return diamondEps;
	}

	public void setDiamondEps(double diamondEps) {
		this.diamondEps = diamondEps;
	}
	
	


}
