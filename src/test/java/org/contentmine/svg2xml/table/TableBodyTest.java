package org.contentmine.svg2xml.table;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

/** 
 * test for TableBodyChunk
 * @author pm286
 *
 */
public class TableBodyTest {

	private final static Logger LOG = Logger.getLogger(TableBodyTest.class);
	
	private final static String TABLE = "target/table";

	@Test
	public void testTDBlockValue() {
		TableChunk genericChunk = TableFixtures.createGenericChunkFromElements(TableFixtures.TDBLOCKFILE);
		String value = genericChunk.getValue();
		Assert.assertEquals("value", "IN6127445.72.92IN56(WT)23065.13.24IN1604729.53.28IN6213654.33.42IN705254.53.86IN575347.04.25IN6911945.04.38IN6320941.24.55IN646348.44.60IN6815354.15.14IN6618982.25.87IN6721257.66.71IN653383.86.95IN714968.87.67", value);
	}
	
	@Test
	public void testCreateRows() {
		TableChunk genericChunk = TableFixtures.createGenericChunkFromElements(TableFixtures.TDBLOCKFILE);
		TableBody tableBody = new TableBody(genericChunk.getElementList());
		List<TableRow> tableRowList = tableBody.createUnstructuredRows();
		Assert.assertEquals("rows", 14, tableRowList.size());
		String[] values = {
			"IN6127445.72.92",
			"IN56(WT)23065.13.24",
			"IN1604729.53.28",
			"IN6213654.33.42",
			"IN705254.53.86",
			"IN575347.04.25",
			"IN6911945.04.38",
			"IN6320941.24.55",
			"IN646348.44.60",
			"IN6815354.15.14",
			"IN6618982.25.87",
			"IN6721257.66.71",
			"IN653383.86.95",
			"IN714968.87.67",
		};
		for (int i = 0; i < tableRowList.size(); i++) {
			TableRow row = tableRowList.get(i);
			Assert.assertEquals("val"+1, values[i], row.getValue());
		}
	}
	
	@Test
	public void testCreateStructuredRows() {
		TableChunk genericChunk = TableFixtures.createGenericChunkFromElements(TableFixtures.TDBLOCKFILE);
		TableBody tableBody = new TableBody(genericChunk.getElementList());
		List<TableRow> rowList = tableBody.createStructuredRows();
		Assert.assertEquals("rows", 14, rowList.size());
		String[] rows = {
			"{{IN61}{274}{45.7}{2.92}}",
			"{{IN56(WT)}{230}{65.1}{3.24}}",
			"{{IN160}{47}{29.5}{3.28}}",
			"{{IN62}{136}{54.3}{3.42}}",
			"{{IN70}{52}{54.5}{3.86}}",
			"{{IN57}{53}{47.0}{4.25}}",
			"{{IN69}{119}{45.0}{4.38}}",
			"{{IN63}{209}{41.2}{4.55}}",
			"{{IN64}{63}{48.4}{4.60}}",
			"{{IN68}{153}{54.1}{5.14}}",
			"{{IN66}{189}{82.2}{5.87}}",
			"{{IN67}{212}{57.6}{6.71}}",
			"{{IN65}{33}{83.8}{6.95}}",
			"{{IN71}{49}{68.8}{7.67}}",
		};
		for (int i = 0; i < rowList.size(); i++) {
			TableRow row = rowList.get(i);
			Assert.assertEquals("row"+i, rows[i], row.toString());
		}
	}
	
//	@Test
//	public void testCreateHtml() {
//		TableChunk genericChunk = TableFixtures.createGenericChunkFromElements(TableFixtures.TDBLOCKFILE);
//		TableBody tableBody = new TableBody(genericChunk.getElementList());
//		HtmlElement rowBody = tableBody.createHtmlElement();
//		Assert.assertEquals("body",
////				"<table xmlns=\"http://www.w3.org/1999/xhtml\"><tr><td><p><span>IN61</span></p></td><td><p><span>274</span></p></td><td><p><span>45.7</span></p></td><td><p><span>2.92</span></p></td></tr><tr><td><p><span>IN56 (WT)</span></p></td><td><p><span>230</span></p></td><td><p><span>65.1</span></p></td><td><p><span>3.24</span></p></td></tr><tr><td><p><span>IN160</span></p></td><td><p><span>47</span></p></td><td><p><span>29.5</span></p></td><td><p><span>3.28</span></p></td></tr><tr><td><p><span>IN62</span></p></td><td><p><span>136</span></p></td><td><p><span>54.3</span></p></td><td><p><span>3.42</span></p></td></tr><tr><td><p><span>IN70</span></p></td><td><p><span>52</span></p></td><td><p><span>54.5</span></p></td><td><p><span>3.86</span></p></td></tr><tr><td><p><span>IN57</span></p></td><td><p><span>53</span></p></td><td><p><span>47.0</span></p></td><td><p><span>4.25</span></p></td></tr><tr><td><p><span>IN69</span></p></td><td><p><span>119</span></p></td><td><p><span>45.0</span></p></td><td><p><span>4.38</span></p></td></tr><tr><td><p><span>IN63</span></p></td><td><p><span>209</span></p></td><td><p><span>41.2</span></p></td><td><p><span>4.55</span></p></td></tr><tr><td><p><span>IN64</span></p></td><td><p><span>63</span></p></td><td><p><span>48.4</span></p></td><td><p><span>4.60</span></p></td></tr><tr><td><p><span>IN68</span></p></td><td><p><span>153</span></p></td><td><p><span>54.1</span></p></td><td><p><span>5.14</span></p></td></tr><tr><td><p><span>IN66</span></p></td><td><p><span>189</span></p></td><td><p><span>82.2</span></p></td><td><p><span>5.87</span></p></td></tr><tr><td><p><span>IN67</span></p></td><td><p><span>212</span></p></td><td><p><span>57.6</span></p></td><td><p><span>6.71</span></p></td></tr><tr><td><p><span>IN65</span></p></td><td><p><span>33</span></p></td><td><p><span>83.8</span></p></td><td><p><span>6.95</span></p></td></tr><tr><td><p><span>IN71</span></p></td><td><p><span>49</span></p></td><td><p><span>68.8</span></p></td><td><p><span>7.67</span></p></td></tr></table>",
//// NOT YET RIGHT
//				"<table xmlns=\"http://www.w3.org/1999/xhtml\"><tr><td><div>IN61 </div></td><td><div>274 </div></td><td><div>45.7 </div></td><td><div>2.92 </div></td></tr><tr><td><div>IN56 (WT) </div></td><td><div>230 </div></td><td><div>65.1 </div></td><td><div>3.24 </div></td></tr><tr><td><div>IN160 </div></td><td><div>47 </div></td><td><div>29.5 </div></td><td><div>3.28 </div></td></tr><tr><td><div>IN62 </div></td><td><div>136 </div></td><td><div>54.3 </div></td><td><div>3.42 </div></td></tr><tr><td><div>IN70 </div></td><td><div>52 </div></td><td><div>54.5 </div></td><td><div>3.86 </div></td></tr><tr><td><div>IN57 </div></td><td><div>53 </div></td><td><div>47.0 </div></td><td><div>4.25 </div></td></tr><tr><td><div>IN69 </div></td><td><div>119 </div></td><td><div>45.0 </div></td><td><div>4.38 </div></td></tr><tr><td><div>IN63 </div></td><td><div>209 </div></td><td><div>41.2 </div></td><td><div>4.55 </div></td></tr><tr><td><div>IN64 </div></td><td><div>63 </div></td><td><div>48.4 </div></td><td><div>4.60 </div></td></tr><tr><td><div>IN68 </div></td><td><div>153 </div></td><td><div>54.1 </div></td><td><div>5.14 </div></td></tr><tr><td><div>IN66 </div></td><td><div>189 </div></td><td><div>82.2 </div></td><td><div>5.87 </div></td></tr><tr><td><div>IN67 </div></td><td><div>212 </div></td><td><div>57.6 </div></td><td><div>6.71 </div></td></tr><tr><td><div>IN65 </div></td><td><div>33 </div></td><td><div>83.8 </div></td><td><div>6.95 </div></td></tr><tr><td><div>IN71 </div></td><td><div>49 </div></td><td><div>68.8 </div></td><td><div>7.67 </div></td></tr></table>",
////		"<table xmlns=\"http://www.w3.org/1999/xhtml\">" +
////		"<tr><td>IN61</td><td>274</td><td>45.7</td><td>2.92</td></tr>" +
////		"<tr><td>IN56(WT)</td><td>230</td><td>65.1</td><td>3.24</td></tr>" +
////		"<tr><td>IN160</td><td>47</td><td>29.5</td><td>3.28</td></tr>" +
////		"<tr><td>IN62</td><td>136</td><td>54.3</td><td>3.42</td></tr>" +
////		"<tr><td>IN70</td><td>52</td><td>54.5</td><td>3.86</td></tr>" +
////		"<tr><td>IN57</td><td>53</td><td>47.0</td><td>4.25</td></tr>" +
////		"<tr><td>IN69</td><td>119</td><td>45.0</td><td>4.38</td></tr>" +
////		"<tr><td>IN63</td><td>209</td><td>41.2</td><td>4.55</td></tr>" +
////		"<tr><td>IN64</td><td>63</td><td>48.4</td><td>4.60</td></tr>" +
////		"<tr><td>IN68</td><td>153</td><td>54.1</td><td>5.14</td></tr>" +
////		"<tr><td>IN66</td><td>189</td><td>82.2</td><td>5.87</td></tr>" +
////		"<tr><td>IN67</td><td>212</td><td>57.6</td><td>6.71</td></tr>" +
////		"<tr><td>IN65</td><td>33</td><td>83.8</td><td>6.95</td></tr>" +
////		"<tr><td>IN71</td><td>49</td><td>68.8</td><td>7.67</td></tr>" +
////		"</table>",
//		rowBody.toXML());
//
//		HtmlTable table = (HtmlTable) tableBody.createHtmlElement();
//		table.setBorder(1);
//		try {
//			new File(TABLE+"/bmc174").mkdirs();
//			FileOutputStream fos = new FileOutputStream(TABLE+"/bmc174/table1.html");
//			SVGUtil.debug(table, fos, 1);
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//
//	}
	
}
