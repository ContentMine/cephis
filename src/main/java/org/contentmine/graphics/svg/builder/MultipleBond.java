package org.contentmine.graphics.svg.builder;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGLine;

/** holds multiple bond partners until we can sort them out later.
 * 
 * @author pm286
 *
 */
public class MultipleBond {
	private static final Logger LOG = Logger.getLogger(MultipleBond.class);
	
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private SVGLine linei;
	private SVGLine linej;

	public MultipleBond(SVGLine linei, SVGLine linej) {
		this.linei = linei;
		this.linej = linej;
	}


}
