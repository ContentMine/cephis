/**
 *    Copyright 2011 Peter Murray-Rust et. al.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.contentmine.graphics.html;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import nu.xom.Elements;


/** base class for lightweight generic SVG element.
 * no checking - i.e. can take any name or attributes
 * @author pm286
 *
 */
public class HtmlTbody extends HtmlElement {
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(HtmlTbody.class);
	public final static String TAG = "tbody";

	/** constructor.
	 * 
	 */
	public HtmlTbody() {
		super(TAG);
	}
	
    public void addRow(HtmlTr row) {
		this.appendChild(row);
	}
	
	public List<HtmlElement> getRows() {
		return getChildElements(this, HtmlTr.TAG);
	}
        
    public List<HtmlTr> getChildTrs() {
        List<HtmlTr> rowList = new ArrayList<HtmlTr>();
        List<HtmlElement> rows = getChildElements(this, HtmlTr.TAG);
        for (HtmlElement el : rows) {
            rowList.add((HtmlTr) el);
        }
        return rowList;
    }
    
    public List<HtmlElement> getChildElementsList() {
        Elements elts = this.getChildElements();
        
        List<HtmlElement> elements = new ArrayList<HtmlElement>();
        for (int i = 0; i < elts.size(); i++) {
            elements.add((HtmlElement) elts.get(i));
        }
        return elements;
    }
}
