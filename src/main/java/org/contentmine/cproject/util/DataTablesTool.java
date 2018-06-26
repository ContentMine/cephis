package org.contentmine.cproject.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.html.HtmlA;
import org.contentmine.graphics.html.HtmlBody;
import org.contentmine.graphics.html.HtmlButton;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlElement.Target;
import org.contentmine.graphics.html.HtmlHead;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.html.HtmlScript;
import org.contentmine.graphics.html.HtmlTable;
import org.contentmine.graphics.html.HtmlTbody;
import org.contentmine.graphics.html.HtmlTd;
import org.contentmine.graphics.html.HtmlTfoot;
import org.contentmine.graphics.html.HtmlTh;
import org.contentmine.graphics.html.HtmlThead;
import org.contentmine.graphics.html.HtmlTr;

public class DataTablesTool {

	private static final Logger LOG = Logger.getLogger(DataTablesTool.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public final static String ASPNET_AJAX = "http://ajax.aspnetcdn.com/ajax/";
	public static final String JQUERY_DATA_TABLES_MIN_JS = ASPNET_AJAX+"jquery.dataTables/1.9.4/jquery.dataTables.min.js";
	public static final String JQUERY_1_8_2_MIN_JS = ASPNET_AJAX+"jQuery/jquery-1.8.2.min.js";
	public static final String JQUERY_DATA_TABLES_CSS = ASPNET_AJAX+"jquery.dataTables/1.9.4/css/jquery.dataTables.css";
	public final static String DATA_TABLE_FUNCTION0 = ""
	+ "$(function()  {\n"
	+ "$(\"#";
	public final static String DATA_TABLE_FUNCTION1 = ""
	+ "\").dataTable();\n"
	+ "})\n";
//	+ "	var node = document.getElementById('node-id');"
//	+ " node.innerHTML('<p>some dynamic html</p>');";	
	public static final String TABLE = "table";
	public static final String TABLE_STRIPED = "table-striped";
	public static final String TABLE_BORDERED = "table-bordered";
	public static final String TABLE_HOVER = "table-hover";
	
	public static final String TARGET = "target";
	
	private static final String RESULTS = "results";
	public static final String ARTICLES = "articles";
	private static final String DEFAULTS = 
			    DataTablesTool.TABLE+
			" "+DataTablesTool.TABLE_STRIPED+
			" "+DataTablesTool.TABLE_BORDERED+
			" "+DataTablesTool.TABLE_HOVER;
	private static final String BS_EXAMPLE_TABLE_RESPONSIVE = "bs-example table-responsive";

	private String title;
	private String tableId; // HTML ID of table element

	public List<CellRenderer> cellRendererList;
	private List<String> rowHeadingList;
	private String rowHeadingName;
	private CellCalculator cellCalculator;
	private String remoteLink0;
	private String remoteLink1;
	private String localLink0;
	private String localLink1;
	private List<HtmlTd> footerCells;
	private HtmlTd footerCaption;
	private String rowLabelId;

	public DataTablesTool() {
		this.setTableId(RESULTS);
		setDefaults();
	}

	public DataTablesTool(String rowLabelId) {
		this();
		this.setRowLabelId(rowLabelId);
	}

	private void setDefaults() {
		remoteLink0 = "http://epmc.org/";
		remoteLink1 = "";
		localLink0 = "";
		localLink1 = "/scholarly.html";
	}

	public DataTablesTool(CellCalculator cellCalculator) {
		this();
		this.setCellCalculator(cellCalculator);
	}
	
	public void setCellCalculator(CellCalculator cellCalculator) {
		this.cellCalculator = cellCalculator;
	}

	public void makeDataTableHead(HtmlHtml html) {
		HtmlHead head = html.getOrCreateHead();
		head.addTitle(title);
		head.addCSSStylesheetLink(JQUERY_DATA_TABLES_CSS);
		head.addJavascriptLink(JQUERY_1_8_2_MIN_JS);
		head.addJavascriptLink(JQUERY_DATA_TABLES_MIN_JS);
		String script = DATA_TABLE_FUNCTION0 + tableId + DATA_TABLE_FUNCTION1;
		head.addJavascript(script);
	}

	public HtmlTd createHyperlinkedCell(String remoteHref, String localHref, String aValue) {
		HtmlTd htmlTd = new HtmlTd();
		createA(remoteHref, aValue, htmlTd);
		createA(localHref, "local", htmlTd);
		return htmlTd;
	}

	private void createA(String href, String aValue, HtmlTd htmlTd) {
		if (href != null && href.trim().length() > 0) {
			HtmlA htmlA = new HtmlA();
			htmlA.appendChild(aValue);
			htmlA.setHref(href);
			htmlA.setTarget(Target.separate);
			htmlTd.appendChild(htmlA);
		}
	}

	public HtmlThead createHtmlHead() {
		HtmlThead htmlThead = new HtmlThead();
		HtmlTr htmlTr = new HtmlTr();
		htmlThead.appendChild(htmlTr);
		htmlTr.appendChild(createColumnHeading(this.getRowLabelId()));
		addRemainingColumnHeadings(htmlTr);
		return htmlThead;
	}

	private HtmlTh createColumnHeading(String id) {
		HtmlTh htmlTh = new HtmlTh();
		htmlTh.appendChild(id);
		return htmlTh;
	}

	private void addRemainingColumnHeadings(HtmlTr htmlTr) {
		
		for (CellRenderer renderer : cellRendererList) {
			if (renderer.isVisible()) {
				HtmlTh htmlTh = new HtmlTh();
				htmlTr.appendChild(htmlTh);
//				htmlTh.appendChild(renderer.getHtmlElement());
				htmlTh.appendChild(renderer.getFlag());
			}
		}
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setTableId(String id) {
		this.tableId = id;
	}
	
	public String getId() {
		return tableId;
	}

	public void setRowHeadingList(List<String> rowHeadingList) {
		this.rowHeadingList = rowHeadingList;
	}

	public List<String> getOrCreateRowHeadingList() {
		if (rowHeadingList == null) {
			rowHeadingList = new ArrayList<String>();
		}
		return rowHeadingList;
	}

	public void setCellRendererList(List<CellRenderer> cellRendererList) {
		this.cellRendererList = cellRendererList;
	}

	public List<CellRenderer> getOrCreateColumnHeadingList() {
		if (cellRendererList == null) {
			cellRendererList = new ArrayList<CellRenderer>();
		}
		return cellRendererList;
	}

	/** this calls addCellValues(htmlTr, rowHeading) which includes ResultsAnalysis logic.
	 * 
	 * @param cellCalculator TODO
	 * @param remoteLink0
	 * @param remoteLink1
	 * @param htmlTbody
	 */
	public void addRows(HtmlTbody htmlTbody) {
		for (int iRow = 0; iRow < rowHeadingList.size(); iRow++) {
			String rowHeading = rowHeadingList.get(iRow);
			HtmlTr htmlTr = new HtmlTr();
			htmlTbody.appendChild(htmlTr);
			String remoteHref = ((remoteLink0 == null) ? "" : remoteLink0) + rowHeading + ((remoteLink1 == null) ? "" : remoteLink1);
			String localHref = ((localLink0 == null) ? "" : localLink0) + rowHeading + ((localLink1 == null) ? "" : localLink1);
			HtmlTd htmlTd = createHyperlinkedCell(remoteHref, localHref, rowHeading);
			htmlTd.setTitle("foo");
			
			htmlTr.appendChild(htmlTd);
			cellCalculator.addCellValues(cellRendererList, htmlTr, iRow);
		}
	}

	public void addCellValuesToRow(HtmlTr htmlTr, int iRow) {
		for (int iCol = 0; iCol < cellRendererList.size(); iCol++) {
			HtmlElement htmlTd = new HtmlTd();
			htmlTr.appendChild(htmlTd);
			HtmlElement contents = cellCalculator.createCellContents(iRow, iCol);
//			contents = contents == null ? "" : contents;
			if (contents != null) {
				htmlTd.appendChild(contents);
			}
		}
	}

	public HtmlTable createHtmlDataTable() {
		HtmlTable htmlTable = new HtmlTable();
		htmlTable.appendChild(createHtmlHead());
		HtmlTbody htmlTbody = new HtmlTbody();
		htmlTable.appendChild(htmlTbody);
		addRows(htmlTbody);
		addFooter(htmlTable);
		return htmlTable;
	}

	private HtmlTfoot addFooter(HtmlTable htmlTable) {
		HtmlTfoot htmlTfoot = new HtmlTfoot();
		if (footerCaption == null || footerCells == null) {
			LOG.trace(""
					+ ""
					+ "aption or cells");
		} else if (footerCells.size() != cellRendererList.size()) {
			LOG.error("Wrong number of footer cells: "+footerCells.size()+" != "+cellRendererList.size());
			return null;
		} else {
			HtmlTr tr = new HtmlTr();
			htmlTfoot.appendChild(tr);
			tr.appendChild(footerCaption);
			for (int i = 0; i < cellRendererList.size(); i++) {
				tr.appendChild(footerCells.get(i));
			}
		}
		htmlTable.appendChild(htmlTfoot);
		return htmlTfoot;
	}

	private void addDiv(HtmlTd td, int i) {
		HtmlDiv div = new HtmlDiv();
		div.appendChild("D"+i);
		td.appendChild(div);
	}

	public HtmlHtml createHtmlWithDataTable(HtmlTable table) {
		HtmlHtml html = HtmlHtml.createUTF8Html();
		makeDataTableHead(html);
		HtmlDiv htmlDiv = new HtmlDiv();
		htmlDiv.setClassAttribute(BS_EXAMPLE_TABLE_RESPONSIVE);
		html.getOrCreateBody().appendChild(htmlDiv);
		htmlDiv.appendChild(table);
		return html;
	}

	private void addButton(HtmlBody body) {
		HtmlButton button = new HtmlButton("Click me");
		button.setOnClick("testFunction(alert('click!!'));");
		body.appendChild(button);
	}

	private void addScript(HtmlBody body) {
		String scriptString = ""
				+ "var node = document.getElementById('footer-id');\n"
				+ "node.innerHTML('<p>some dynamic html</p>');";
		HtmlScript script = new HtmlScript();
		script.setCharset("UTF-8");
		script.setType("text/javascript");
		script.appendChild(scriptString);
		body.appendChild(script);
	}

	public void setRowHeadingName(String rowHeadingName) {
		this.rowHeadingName = rowHeadingName;
	}

	public void setRemoteLink0(String link0) {
		this.remoteLink0 = link0;
	}

	public String getRemoteLink0() {
		return remoteLink0;
	}
	
	public void setRemoteLink1(String link1) {
		this.remoteLink1 = link1;
	}

	public String getRemoteLink1() {
		return remoteLink1;
	}

	public void setLocalLink0(String link0) {
		this.localLink0 = link0;
	}

	public String getLocalLink0() {
		return localLink0;
	}
	
	public void setLocalLink1(String link1) {
		this.localLink1 = link1;
	}

	public String getLocalLink1() {
		return localLink1;
	}

	public HtmlHtml createHtml(CellCalculator cellCalculator) {
		HtmlTable htmlTable = createHtmlDataTable();
		htmlTable.setClassAttribute(DataTablesTool.DEFAULTS);
		htmlTable.setId(tableId);
		HtmlHtml html = createHtmlWithDataTable(htmlTable);
		return html;
	}
	
	public void setFooterCaption(HtmlTd caption) {
		this.footerCaption = caption;
	}
	
	public void setFooterCells(List<HtmlTd> cells) {
		this.footerCells = cells;
	}
	
	public String getRowLabelId() {
		return rowLabelId;
	}

	public void setRowLabelId(String rowLabelId) {
		this.rowLabelId = rowLabelId;
	}

}
