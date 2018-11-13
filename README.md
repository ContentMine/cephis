# cephis

Cephis is a library composed of 6 historical libraries (the name is an acronym). They were originally developed as separate
modules but this became unwieldy to support versions and hierarchy so they have been merged into a single module. The modules 
are somewhat preserved and could be called from other packages such as the chemical JUMBOConverters but their current use is for
suporting Norma and AMI.

## euclid
`org.contentmine.eucl.euclid`

Utility routines dating from when Java had few libraries. Some could be replaced. They include:

* arrays and lists
* 2- and 3-D geometry
* String operations
* File operations

## html
`org.contentmine.graphics.html`

A DOM for HTML. Unlike most DOMs which are created to render objects this is designed for creating the objects from non-semantic primitives.
Most HTML elements which support objects and containment are present, but style objects are generally missing. We aim towards HTML5.

There is special support for building HTML from the output of OCR (HOCR).

## svg
`org.contentmine.graphics.svg`

A DOM for SVG. Unlike most DOMs which are created to render objects this is designed for creating the objects from non-semantic primitives.
Most SVG elements which support static objects and containment are present, but style and animations objects are generally missing. There is  much support for creating higher primitives from lower, thus:
```path => line => rectangle => textbox```

## font
`org.contentmine.font`

Support for managing font information, including non-unicode characters. Messy since PDF is messy.

## image
`org.contentmine.image`

Support for processing an interpreting pixel images (bitmaps). Uses BoofCV for some of the orginal processing but then adds layers to creat 
objects heuristically:
``` pixels => islands => graphs => lines => shapes => SVG objects ```

## pdf2svg
`org.contentmine.pdf2svg` and `org.contentmine.pdf2svg2`

Routines trapping the PDFBox primitives and creating SVG. Generally creates low level primitives 
``` path character image```
and then attempts to make geometrical objects and text (images are untouched)

## svg2xml 
creates HTML from SVG

## cproject
`org.contentmine.cproject`

manages a file-based data structure for the result of parsing. A `CProject` is a directory containing `CTree`s which themselves may have many specific sub-directories. 
