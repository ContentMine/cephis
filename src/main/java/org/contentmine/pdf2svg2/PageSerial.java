package org.contentmine.pdf2svg2;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/** identifier for page and subcomponents
 * 
 * currently pageSerial[.optionalSub]
 * 
 * */

public class PageSerial implements Comparable {
	private static final Logger LOG = Logger.getLogger(PageSerial.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private Integer page;
	private Integer subPage;

	public PageSerial(int page) {
		this.page = page;
	}

	public PageSerial(int page, int subPage) {
		this.page = page;
		this.subPage = subPage;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getSubPage() {
		return subPage;
	}

	public void setSubPage(Integer subPage) {
		this.subPage = subPage;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((page == null) ? 0 : page.hashCode());
		result = prime * result + ((subPage == null) ? 0 : subPage.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PageSerial other = (PageSerial) obj;
		if (page == null) {
			if (other.page != null)
				return false;
		} else if (!page.equals(other.page))
			return false;
		if (subPage == null) {
			if (other.subPage != null)
				return false;
		} else if (!subPage.equals(other.subPage))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PageSerial [page=" + page + ", subPage=" + subPage + "]";
	}

	/** 
	 *  this.serial smaller than o.serial, return -1
	 *  this.serial larger than o.serial,  return 1
	 *  this.serial equal to o.serial
	 *    if this.subSerial smaller than o.subSerial return -1
	 *    if this.subSerial larger  than o.subSerial return 1
	 *    if this.subSerial equals  to   o.subSerial return 0
	 *    if this.subSerial null and o.subSerial null return 0
	 */
	@Override
	public int compareTo(Object o) {
		if (o instanceof PageSerial) {
			PageSerial p = (PageSerial) o;
			if (this.page < p.page) return -1;
			if (this.page > p.page) return 1;
			if (this.subPage == null && p.subPage == null) return 0;
			if (this.subPage == null && p.subPage != null) return -1;
			if (this.subPage != null && p.subPage == null) return 1;
			return this.subPage - p.subPage;
		}
		return 0;
	}
	
	public String getSerialString() {
		String pageS = String.valueOf(page);
		if (subPage != null) {
			pageS += "."+subPage;
		}
		return pageS;
	}
	
}
