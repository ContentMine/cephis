package org.contentmine.graphics.svg.plot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2Array;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.SVGUtil;
import org.contentmine.graphics.svg.SVGLine.LineDirection;
import org.contentmine.graphics.svg.cache.AbstractCache;
import org.contentmine.graphics.svg.cache.ComponentCache;
import org.contentmine.graphics.svg.cache.LineCache;

/** superclass of all plotBox types.
 * current types include XY, X, Y Plots
 * 
 * @author pm286
 *
 */
public abstract class AbstractPlotBox {
	protected static final Logger LOG = Logger.getLogger(AbstractPlotBox.class);
	public static int FORMAT_NDEC = 3;
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public enum AxisType {
		BOTTOM(0, LineDirection.HORIZONTAL, 1),
		LEFT(1, LineDirection.VERTICAL, -1),
		TOP(2, LineDirection.HORIZONTAL, -1),
		RIGHT(3, LineDirection.VERTICAL, 1);
		private int serial;
		private LineDirection direction;
		/** if 1 adds outsideWidth to maxBox, else if -1 adds insideWidth */
		private int outsidePositive;
		private AxisType(int serial, LineDirection direction, int outsidePositive) {
			this.serial = serial;
			this.direction = direction;
			this.outsidePositive = outsidePositive;
		}
		
		public int getSerial() {
			return serial;
		}
		public static int getSerial(AxisType axisType) {
			for (int i = 0; i < values().length; i++) {
				if (values()[i].equals(axisType)) {
					return i;
				}
			}
			return -1;
		}
		public static final int BOTTOM_AXIS = AxisType.getSerial(AxisType.BOTTOM);
		public static final int LEFT_AXIS   = AxisType.getSerial(AxisType.LEFT);
		public static final int TOP_AXIS    = AxisType.getSerial(AxisType.TOP);
		public static final int RIGHT_AXIS  = AxisType.getSerial(AxisType.RIGHT);
		public LineDirection getLineDirection() {
			return direction;
		}
		/** 
		 * 
		 * @return if 1 adds outsideWidth to max dimension of initial box and
		 *                   insideWidth min dimension
		 *         if 0 adds outsideWidth to min dimension of initial box and
		 *                   insideWidth max dimension
		 *                   
		 *   
		 */
		public int getOutsidePositive() {
			return outsidePositive;
		}
	}

	public enum BoxType {
		HLINE("bottom x-axis only"), 
		UBOX("bottom x-axis L-y-axis R-y-axis"),
		PIBOX("top x-axis L-y-axis R-y-axis"),
		LBOX("bottom x-axis L-y-axis"),
		RBOX("bottom x-axis R-y-axis"),
		FULLBOX("bottom x-axis top x-axis L-y-axis R-y-axis"),
		;
		private String title;
		private BoxType(String title) {
			this.title = title;
		}
	}

	protected AnnotatedAxis[] axisArray;
	protected BoxType boxType;
	protected int ndecimal = FORMAT_NDEC;
	protected File svgOutFile;
	protected String csvContent;
	protected File csvOutFile;
	protected ComponentCache componentCache;
	protected String fileRoot;
	protected Real2Array screenXYs;
	protected Real2Array scaledXYs;
	protected RealArray screenYs;
	protected RealArray scaledYs;
	protected RealArray screenXs;
	protected RealArray scaledXs;
	protected List<String> xTitles;
	protected List<String> yTitles;

	protected void setDefaults() {
		axisArray = new AnnotatedAxis[AxisType.values().length];
		for (AxisType axisType : AxisType.values()) {
			AnnotatedAxis axis = createAxis(axisType);
			axisArray[axisType.getSerial()] = axis;
		}
		ndecimal = FORMAT_NDEC;
	}

	AnnotatedAxis createAxis(AxisType axisType) {
		AnnotatedAxis axis = new AnnotatedAxis(this, axisType);
		return axis;
	}

	public ComponentCache getComponentCache() {
		return componentCache;
	}

	/** MAIN ENTRY METHOD for processing plots.
	 * 
	 * @param originalSvgElement
	 * @throws FileNotFoundException 
	 */
	public void readAndCreateCSVPlot(File file) throws FileNotFoundException {
		InputStream inputStream = new FileInputStream(file);
		this.fileRoot = FilenameUtils.getName(file.toString());
		readAndCreateCSVPlot(inputStream);
	}

	/** MAIN ENTRY METHOD for processing plots.
	 * 
	 * @param inputStream
	 */
	private void readAndCreateCSVPlot(InputStream inputStream) {
		if (inputStream == null) {
			throw new RuntimeException("Null input stream");
		}
		AbstractCMElement svgElement = SVGUtil.parseToSVGElement(inputStream);
		if (svgElement == null) {
			throw new RuntimeException("Null svgElement");
		}
		readAndCreateCSVPlot(svgElement);
	}

	/** ENTRY METHOD for processing figures.
	 * 
	 * @param originalSvgElement
	 */
	public void readGraphicsComponents(File inputFile) {
		if (inputFile == null) {
			throw new RuntimeException("Null input file");
		}
		if (!inputFile.exists() || inputFile.isDirectory()) {
			throw new RuntimeException("nonexistent file or isDirectory "+inputFile);
		}
		fileRoot = inputFile.getName();
		componentCache = new ComponentCache(this);
		try {
			componentCache.readGraphicsComponentsAndMakeCaches(new FileInputStream(inputFile));
		} catch (IOException e) {
			throw new RuntimeException("Cannot read inputFile", e);
		}
	}

	protected abstract void readAndCreateCSVPlot(AbstractCMElement svgElement);

	protected void extractDataScreenPoints() {
		// TODO Auto-generated method stub
		
	}

	protected String createCSVContent() {
		return csvContent;
	}

	protected void makeAxialTickBoxesAndPopulateContents() {
		LineCache lineCache = componentCache.getOrCreateLineCache();
		for (AnnotatedAxis axis : axisArray) {
			axis.getOrCreateSingleLine();		
			axis.createAndFillTickBox(lineCache.getOrCreateHorizontalLineList(), lineCache.getOrCreateVerticalLineList());
		}
	}

	protected void extractScaleTextsAndMakeScales() {
		for (AnnotatedAxis axis : this.axisArray) {
			axis.extractScaleTextsAndMakeScales();
		}
	}

	protected void extractTitleTextsAndMakeTitles() {
		for (AnnotatedAxis axis : this.axisArray) {
			axis.extractTitleTextsAndMakeTitles();
		}
	}

	protected void makeRangesForAxes() {
		for (AnnotatedAxis axis : this.axisArray) {
			axis.createAxisRanges();
		}
	}

	public void writeCSV(File file) {
		if (file != null) {
			try {
				IOUtils.write(csvContent, new FileOutputStream(file));
			} catch (IOException e) {
				throw new RuntimeException("cannot write CSV: ", e);
			}
		}
	}

	private AbstractCMElement copyAnnotatedAxes() {
		SVGG g = new SVGG();
		g.setSVGClassName("plotBox");
		for (AnnotatedAxis axis : axisArray) {
			g.appendChild(axis.getSVGElement().copy());
		}
		return g;
	}

	public BoxType getBoxType() {
		return boxType;
	}

	public void setBoxType(BoxType boxType) {
		this.boxType = boxType;
	}

	public AnnotatedAxis[] getAxisArray() {
		return axisArray;
	}

	public int getNdecimal() {
		return ndecimal;
	}

	public void setNdecimal(int ndecimal) {
		this.ndecimal = ndecimal;
	}

	public List<SVGText> getHorizontalTexts() {
		return componentCache.getOrCreateTextCache().getOrCreateHorizontalTexts();
	}

	public List<SVGText> getVerticalTexts() {
		return componentCache.getOrCreateTextCache().getOrCreateVerticalTexts();
	}

	public void writeProcessedSVG(File file) {
		if (file != null) {
			SVGElement processedSVGElement = componentCache.createSVGElement();
			processedSVGElement.appendChild(copyAnnotatedAxes());
			SVGSVG.wrapAndWriteAsSVG(processedSVGElement, file);
		}
	}

	public String getCSV() {
		return csvContent;
	}

	public File getSvgOutFile() {
		return svgOutFile;
	}

	public void setSvgOutFile(File svgOutFile) {
		this.svgOutFile = svgOutFile;
	}

	public File getCsvOutFile() {
		return csvOutFile;
	}

	public void setCsvOutFile(File csvOutFile) {
		this.csvOutFile = csvOutFile;
	}

	public void readAndStructureFigures(AbstractCMElement svgElement) {
		List<SVGElement> textElements = SVGUtil.getQuerySVGElements(svgElement, ".//*[local-name()='text']");
		List<SVGText> texts = SVGText.extractTexts(textElements);
		LOG.debug("texts "+texts.size());
	}

}
