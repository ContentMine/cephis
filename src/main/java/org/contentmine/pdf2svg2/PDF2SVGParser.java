package org.contentmine.pdf2svg2;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDPattern;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;
import org.apache.pdfbox.pdmodel.graphics.state.PDGraphicsState;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.rendering.PageDrawer;
import org.apache.pdfbox.rendering.PageDrawerParameters;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.Vector;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.graphics.svg.SVGClipPath;
import org.contentmine.graphics.svg.SVGDefs;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGPath;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.SVGUtil;

public class PDF2SVGParser extends PageDrawer    {
	private static final Logger LOG = Logger.getLogger(PDF2SVGParser.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static final String CLIP_PATH = "clipPath";

	private static final Graphics2D TEXT = null;

//	private List<SVGText> textList;
	private SVGG svgg;
	private double yEpsilon = 0.05; // guess
	private double scalesEpsilon = 0.1; // guess
	private int nPlaces = 3;
	
	private SVGText currentSVGText;
	private TextParameters currentTextParameters;
	private Double currentX;
	private Double currentY;
	private TextParameters textParameters;
	private Real2 currentScales;
	private SVGPath currentSvgPath;
//	private List<SVGPath> pathList;
	private String clipString;
	private Real2 currentDisplacement;
	private Real2 currentXY;
	// clipping
	private Map<String, Integer> integerByClipStringMap;
	private Set<String> clipStringSet;
	// defs
	private SVGElement defs1;

	private AMIGraphicsState savedTextState;

	private AMIGraphicsState graphicsState;


	PDF2SVGParser(PageDrawerParameters parameters) throws IOException        {
        super(parameters);
        init();
    }

    private void init() {
//    	textList = new ArrayList<SVGText>();
//		pathList = new ArrayList<SVGPath>();
    	svgg = new SVGG();
    	integerByClipStringMap = new HashMap<String, Integer>();
	}

	/**
     * Color replacement.
     */
    @Override
    protected Paint getPaint(PDColor color) throws IOException {
        // if this is the non-stroking color
        if (getGraphicsState().getNonStrokingColor() == color)
        {
        	LOG.debug("color++++++++++++ "+color);
            // find red, ignoring alpha channel
            if (color.toRGB() == (Color.RED.getRGB() & 0x00FFFFFF))
            {
                // replace it with blue
                return Color.BLUE;
            }
        }
        return super.getPaint(color);
    }

    /**
     * Glyph bounding boxes.
     * This is actually where the codepoint is passed in
     */
    @Override
    /** would be best to set up temporary array for text coordinates and values.
     * also deal with rotated text here
     */
    protected void showGlyph(Matrix textRenderingMatrix, PDFont font, int code, String unicode,
                             Vector displacementxy) throws IOException    {
    	
    	
    	textParameters = new TextParameters(textRenderingMatrix, font);
    	Real2 scales = textParameters.getScales();
    	double x = Util.format((double) textRenderingMatrix.getTranslateX(), nPlaces);
    	double y = Util.format((double) textRenderingMatrix.getTranslateY(), nPlaces);
    	currentXY = new Real2(x, y);
    	currentDisplacement = new Real2(displacementxy.getX(), displacementxy.getY());
//    	double xsep = Util.format((double) displacementxy.getX(), nPlaces);
//    	double ysep = Util.format((double) displacementxy.getY(), nPlaces);
    	if (currentSVGText == null || currentTextParameters == null) {
    		LOG.debug("start");
    		createAndAddCurrentSVGText();
    	} else if (!currentTextParameters.hasEqualFont(textParameters)) {
    		LOG.debug("font changed");
    		createAndAddCurrentSVGText();
    	} else if (isYChanged(y)) {
    		LOG.debug("y changed");
    		createAndAddCurrentSVGText();
    	} else if (isScaleChanged(scales)) {
    		LOG.debug("scale changed");
    		createAndAddCurrentSVGText();
    	} else {
    		// current SVGText is already created and in list
    	}
    	currentSVGText.appendText(unicode);
    	currentSVGText.appendX(x);
    	currentSVGText.setY(y);
		LOG.debug(x+"/"+y+"/"+currentSVGText.toXML());

//        // bbox in EM -> user units
        Shape bbox = new Rectangle2D.Float(0, 0, font.getWidth(code) / 1000, 1);
        AffineTransform at = textRenderingMatrix.createAffineTransform();
        bbox = at.createTransformedShape(bbox);
        
		Graphics2D graphics = getGraphics();
		savedTextState = AMIGraphicsState.createSavedTextState(graphics);

//        // draw
//        graphics.setClip(graphics.getDeviceConfiguration().getBounds());
//        graphics.setColor(Color.RED);
//        graphics.setStroke(new BasicStroke(.5f));
//        graphics.draw(bbox);

//        savedTextState.restoreText(graphics);
        
        currentX = x;
        currentY = y;
        currentTextParameters = textParameters;
		currentScales = scales;

    }

	/** processes both stroke and fill for paths
	 * 
	 * @param windingRule if not null implies fill else stroke
	 * @param currentPaint
	 */
	private void createAndAddSVGPath(Integer windingRule, Paint currentPaint) {
		graphicsState = AMIGraphicsState.createGraphicsState(getGraphicsState());
		GeneralPath generalPath = getLinePath();
		if (windingRule != null) {
			generalPath.setWindingRule(windingRule);
		}
		SVGPath svgPath = new SVGPath(generalPath);
		clipString = getAndFormatClipPath();
		svgPath.setClipPath(clipString);
		setClipPath(svgPath, clipString, integerByClipStringMap.get(clipString));
		if (windingRule != null) {
			svgPath.setFill(getCSSColor(currentPaint));
		} else {
			svgPath.setStroke(getCSSColor(currentPaint));
		}
		if (graphicsState.getDashPattern() != null) {
			setDashArray(svgPath);
		}
		svgPath.setStrokeWidth((double)graphicsState.getLineWidth());
		svgPath.format(nPlaces);
		svgg.appendChild(svgPath);
		generalPath.reset();
	}

//	public void fillPath(int windingRule) throws IOException {
//		PDColor colorState = getGraphicsState().getNonStrokingColor();
//		Paint currentPaint = getCurrentPaint(colorState, "non-stroking");
//		createAndAddSVGPath(windingRule, currentPaint);
//	}
//
//	public void strokePath() throws IOException {
//		PDColor colorState = getGraphicsState().getStrokingColor(); 
//		Paint currentPaint = getCurrentPaint(colorState, "stroking");
//		Integer windingRule = null;
//		createAndAddSVGPath(windingRule, currentPaint);
//	}

	private boolean isScaleChanged(Real2 scales) {
		if (currentScales == null || scales == null) {
			return true;
		}
		return (!currentScales.isEqualTo(scales, scalesEpsilon));
	}

	private boolean isYChanged(Double y) {
		if (currentY == null || y == null) {
			return true;
		}
		double delta = Math.abs(currentY - y);
		return delta > yEpsilon;
	}

	private void createAndAddCurrentSVGText() {
		currentSVGText = new SVGText();
		RealArray xArray = new RealArray();
		currentSVGText.setX(xArray);
		LOG.debug(currentSVGText.toXML());
		svgg.appendChild(currentSVGText);
		PDFont font = textParameters.getFont();
		currentSVGText.setFontFamily(font.getFontDescriptor().getFontFamily());
		currentSVGText.setFontSize(textParameters.getFontSize());
	}

	/**
     * Filled path bounding boxes.
     */
    @Override
    public void fillPath(int windingRule) throws IOException
    {
    	GeneralPath generalPath = getLinePath();
		currentSvgPath = new SVGPath(generalPath);
		currentSvgPath.setFill("none");
		svgg.appendChild(currentSvgPath);
    	LOG.debug("g*************?? "+currentSvgPath);
    	
        // draw path (note that getLinePath() is now reset)
        super.fillPath(windingRule);
        
        // save
        Graphics2D graphics = getGraphics();
        PDGraphicsState graphicsState = getGraphicsState();
        PDColor stroke = graphicsState.getStrokingColor();
        LOG.debug(stroke);
        AMIGraphicsState currentShapeState = AMIGraphicsState.createSavedTextState(graphics);
        

        // draw
        graphics.setClip(graphics.getDeviceConfiguration().getBounds());
        graphics.setColor(Color.GREEN);
        graphics.setStroke(new BasicStroke(.5f));
//        graphics.draw(clip);

        // restore
        currentShapeState.restoreGraphicsState();
//        graphics.setStroke(stroke);
//        graphics.setColor(color);
//        graphics.setClip(clip);
    }

    /**
     * Custom annotation rendering.
     */
    @Override
    public void showAnnotation(PDAnnotation annotation) throws IOException
    {
        // save
        saveGraphicsState();
        
        // 35% alpha
        getGraphicsState().setNonStrokeAlphaConstant(0.35);
        super.showAnnotation(annotation);
        
        // restore
        restoreGraphicsState();
    }
    
    @Override
    public void strokePath() throws IOException {
    	
//        graphics.setComposite(getGraphicsState().getStrokingJavaComposite());
//        graphics.setPaint(getStrokingPaint());
//        graphics.setStroke(getStroke());
//        setClip();
//        //TODO bbox of shading pattern should be used here? (see fillPath)
//        graphics.draw(linePath);
//        linePath.reset();
    }

    
    /**
     * Fills and then strokes the path.
     *
     * @param windingRule The winding rule this path will use.
     * @throws IOException If there is an IO error while filling the path.
     */
    @Override
    public void fillAndStrokePath(int windingRule) throws IOException {
    	super.fillAndStrokePath(windingRule);
    	LOG.debug("fillAndStrokePath");
//        // TODO can we avoid cloning the path?
//        GeneralPath path = (GeneralPath)linePath.clone();
//        fillPath(windingRule);
//        linePath = path;
//        strokePath();
    }

    @Override
    public void clip(int windingRule) {
    	super.clip(windingRule);
    	LOG.debug("clip");
        // the clipping path will not be updated until the succeeding painting operator is called
//        clipWindingRule = windingRule;
    }

    @Override
    public void moveTo(float x, float y) {
    	super.moveTo(x, y);
    	LOG.debug("moveTo");
//        linePath.moveTo(x, y);
    }

    @Override
    public void lineTo(float x, float y)    {
    	super.lineTo(x, y);
    	LOG.debug("lineTo");
//        linePath.lineTo(x, y);
    }

    @Override
    public void curveTo(float x1, float y1, float x2, float y2, float x3, float y3)    {
    	super.curveTo(x1, y1, x2, y2, x3, y3);
    	LOG.debug("curveTo");
//        linePath.curveTo(x1, y1, x2, y2, x3, y3);
    }

    @Override
    public Point2D getCurrentPoint()    {
    	LOG.debug("getCurrentPoint");
    	return super.getCurrentPoint();
//        return linePath.getCurrentPoint();
    }

    @Override
    public void closePath()    {
    	LOG.debug("closePath");
    	super.closePath();
//        linePath.closePath();
    }

    @Override
    public void endPath()    {
    	LOG.debug("endPath");
    	super.endPath();
//
//        if (clipWindingRule != -1)
//        {
//            linePath.setWindingRule(clipWindingRule);
//            getGraphicsState().intersectClippingPath(linePath);
//
//            // PDFBOX-3836: lastClip needs to be reset, because after intersection it is still the same 
//            // object, thus setClip() would believe that it is cached.
//            lastClip = null;
//
//            clipWindingRule = -1;
//        }
//        linePath.reset();
    }
    
    @Override
    public void drawImage(PDImage pdImage) throws IOException    {
    	LOG.debug("drawImage");
    	super.drawImage(pdImage);
    	
//        Matrix ctm = getGraphicsState().getCurrentTransformationMatrix();
//        AffineTransform at = ctm.createAffineTransform();
//
//        if (!pdImage.getInterpolate())
//        {
//            boolean isScaledUp = pdImage.getWidth() < Math.round(at.getScaleX()) ||
//                                 pdImage.getHeight() < Math.round(at.getScaleY());
//
//            // if the image is scaled down, we use smooth interpolation, eg PDFBOX-2364
//            // only when scaled up do we use nearest neighbour, eg PDFBOX-2302 / mori-cvpr01.pdf
//            // stencils are excluded from this rule (see survey.pdf)
//            if (isScaledUp || pdImage.isStencil())
//            {
//                graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
//                        RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
//            }
//        }
//
//        if (pdImage.isStencil())
//        {
//            if (getGraphicsState().getNonStrokingColor().getColorSpace() instanceof PDPattern)
//            {
//                // The earlier code for stencils (see "else") doesn't work with patterns because the
//                // CTM is not taken into consideration.
//                // this code is based on the fact that it is easily possible to draw the mask and 
//                // the paint at the correct place with the existing code, but not in one step.
//                // Thus what we do is to draw both in separate images, then combine the two and draw
//                // the result. 
//                // Note that the device scale is not used. In theory, some patterns can get better
//                // at higher resolutions but the stencil would become more and more "blocky".
//                // If anybody wants to do this, have a look at the code in showTransparencyGroup().
//
//                // draw the paint
//                Paint paint = getNonStrokingPaint();
//                Rectangle2D unitRect = new Rectangle2D.Float(0, 0, 1, 1);
//                Rectangle2D bounds = at.createTransformedShape(unitRect).getBounds2D();
//                BufferedImage renderedPaint = 
//                        new BufferedImage((int) Math.ceil(bounds.getWidth()), 
//                                          (int) Math.ceil(bounds.getHeight()), 
//                                           BufferedImage.TYPE_INT_ARGB);
//                Graphics2D g = (Graphics2D) renderedPaint.getGraphics();
//                g.translate(-bounds.getMinX(), -bounds.getMinY());
//                g.setPaint(paint);
//                g.fill(bounds);
//                g.dispose();
//
//                // draw the mask
//                BufferedImage mask = pdImage.getImage();
//                BufferedImage renderedMask = new BufferedImage((int) Math.ceil(bounds.getWidth()), 
//                                                               (int) Math.ceil(bounds.getHeight()), 
//                                                               BufferedImage.TYPE_INT_RGB);
//                g = (Graphics2D) renderedMask.getGraphics();
//                g.translate(-bounds.getMinX(), -bounds.getMinY());
//                AffineTransform imageTransform = new AffineTransform(at);
//                imageTransform.scale(1.0 / mask.getWidth(), -1.0 / mask.getHeight());
//                imageTransform.translate(0, -mask.getHeight());
//                g.drawImage(mask, imageTransform, null);
//                g.dispose();
//
//                // apply the mask
//                final int[] transparent = new int[4];
//                int[] alphaPixel = null;
//                WritableRaster raster = renderedPaint.getRaster();
//                WritableRaster alpha = renderedMask.getRaster();
//                int h = renderedMask.getRaster().getHeight();
//                int w = renderedMask.getRaster().getWidth();
//                for (int y = 0; y < h; y++)
//                {
//                    for (int x = 0; x < w; x++)
//                    {
//                        alphaPixel = alpha.getPixel(x, y, alphaPixel);
//                        if (alphaPixel[0] == 255)
//                        {
//                            raster.setPixel(x, y, transparent);
//                        }
//                    }
//                }
//                
//                // draw the image
//                setClip();
//                graphics.setComposite(getGraphicsState().getNonStrokingJavaComposite());
//                graphics.drawImage(renderedPaint, 
//                        AffineTransform.getTranslateInstance(bounds.getMinX(), bounds.getMinY()), 
//                        null);
//            }
//            else
//            {
//                // fill the image with stenciled paint
//                BufferedImage image = pdImage.getStencilImage(getNonStrokingPaint());
//
//                // draw the image
//                drawBufferedImage(image, at);
//            }
//        }
//        else
//        {
//            if (subsamplingAllowed)
//            {
//                int subsampling = getSubsampling(pdImage, at);
//                // draw the subsampled image
//                drawBufferedImage(pdImage.getImage(null, subsampling), at);
//            }
//            else
//            {
//                // subsampling not allowed, draw the image
//                drawBufferedImage(pdImage.getImage(), at);
//            }
//        }
//
//        if (!pdImage.getInterpolate())
//        {
//            // JDK 1.7 has a bug where rendering hints are reset by the above call to
//            // the setRenderingHint method, so we re-set all hints, see PDFBOX-2302
//            setRenderingHints();
//        }
    }


	private String getAndFormatClipPath() {
		Shape shape = getGraphicsState().getCurrentClippingPath();
		SVGPath path = new SVGPath(shape);
		path.format(nPlaces);
		clipString = path.getDString();
		// old approach
		ensureClipStringSet();
		clipStringSet.add(clipString);
		// new approach
		ensureIntegerByClipStringMap();
		if (!integerByClipStringMap.containsKey(clipString)) {
			integerByClipStringMap.put(clipString, integerByClipStringMap.size()+1); // count from 1
		}
		return clipString;
	}

    
    /**
     * Returns the underlying Graphics2D. May be null if drawPage has not yet been called.
     */
    protected final Graphics2D getGraphicsxx()
    {
        return super.getGraphics();
    }

	public SVGG getSVGG() {
		return svgg;
	}
    
	private void setDashArray(SVGPath svgPath) {
		@SuppressWarnings("unchecked")
		float[] fDashArray =  graphicsState.getDashPattern().getDashArray();
		StringBuilder sb = new StringBuilder("");
		if (fDashArray != null) {
			for (int i = 0; i < fDashArray.length; i++) {
				if (i > 0) {
					sb.append(" ");
				}
				double d = fDashArray[i]; 
				sb.append(Util.format(d, nPlaces));
			}
			svgPath.setStrokeDashArray(sb.toString());
		}
	}
	
	private void setClipPath(SVGElement svgElement, String clipString, Integer clipPathNumber) {
		String urlString = "url(#clipPath"+clipPathNumber+")";
		svgElement.setClipPath(urlString);
	}
	
	private void createDefsForClipPaths() {
	//   <clipPath clipPathUnits="userSpaceOnUse" id="clipPath14">
//		    <path stroke="black" stroke-width="0.5" fill="none" d="M0 0 L89.814 0 L89.814 113.7113 L0 113.7113 L0 0 Z"/>
//		  </clipPath>
			ensureIntegerByClipStringMap();
			ensureDefs1();
			for (String pathString : integerByClipStringMap.keySet()) {
				Integer serial = integerByClipStringMap.get(pathString);
				SVGClipPath clipPath = new SVGClipPath();
				clipPath.setId(CLIP_PATH+serial);
				defs1.appendChild(clipPath);
				SVGPath path = new SVGPath();
				path.setDString(pathString);
				clipPath.appendChild(path);
			}
		}
	private void ensureIntegerByClipStringMap() {
		if (integerByClipStringMap == null) {
			integerByClipStringMap = new HashMap<String, Integer>();
		}
	}

	private void ensureClipStringSet() {
		if (clipStringSet == null) {
			clipStringSet = new HashSet<String>();
		}
	}

	/** translates java color to CSS RGB
	 * 
	 * @param paint
	 * @return CCC as #rrggbb (alpha is currently discarded)
	 */
	private static String getCSSColor(Paint paint) {
		String colorS = null;
		if (paint instanceof Color) {
			int r = ((Color) paint).getRed();
			int g = ((Color) paint).getGreen();
			int b = ((Color) paint).getBlue();
			// int a = ((Color) paint).getAlpha();
			int rgb = (r<<16)+(g<<8)+b;
			colorS = String.format("#%06x", rgb);
			if (rgb != 0) {
				LOG.trace("Paint "+rgb+" "+colorS);
			}
		}
		return colorS;
	}

	private void ensureDefs1() {
/*
<svg fill-opacity="1" 
xmlns="http://www.w3.org/2000/svg">
  <defs id="defs1">
   <clipPath clipPathUnits="userSpaceOnUse" id="clipPath1">
    <path stroke="black" stroke-width="0.5" fill="none" d="M0 0 L595 0 L595 793 L0 793 L0 0 Z"/>
   </clipPath>
   </defs>
 */
		List<SVGElement> defList = SVGUtil.getQuerySVGElements(svgg, "/svg:g/svg:defs[@id='defs1']");
		defs1 = (defList.size() > 0) ? defList.get(0) : null;
		if (defs1 == null) {
			defs1 = new SVGDefs();
			defs1.setId("defs1");
			svgg.insertChild(defs1, 0);
		}
	}




}
