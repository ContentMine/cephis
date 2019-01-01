package org.contentmine.eucl.euclid.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.eucl.xml.XMLUtil;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableSortedMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;
import com.google.common.collect.Multisets;

import nu.xom.Node;

/** mainly static tools. for managing multisets
 * 
 * originally strongly typed static but being gradually reworked to parameterised.
 * 
 * @author pm286
 *
 */
public class MultisetUtil<T extends Object> {

	public static <T> Iterable<Entry<T>> getEntriesSortedByValue(Multiset<T> set) {
		return  ImmutableSortedMultiset.copyOf(set).entrySet();		
	}
	
	public static <T> Iterable<Multiset.Entry<T>> getEntriesSortedByCount(Multiset<T> objectSet) {
		return Multisets.copyHighestCountFirst(objectSet).entrySet();
	}

	/** extracts a list of attribute values.
	 * 
	 * @return
	 */
	public static List<String> getAttributeValues(Node searchNode, String xpath) {
		List<Node> nodes = XMLUtil.getQueryNodes(searchNode, xpath);
		List<String> nodeValues = new ArrayList<String>();
		for (Node node : nodes) {
			String value = node.getValue();
			if (value != null && value.trim().length() != 0) {
				nodeValues.add(value);
			}
		}
		return nodeValues;
	}

	public static <T> Comparable<T> getLowestValue(Multiset<T> valueSet) {
		Iterable<Multiset.Entry<T>> values = MultisetUtil.getEntriesSortedByValue(valueSet);
		Multiset.Entry<T> entries = values.iterator().hasNext() ? (Multiset.Entry<T>) values.iterator().next() : null;
		Comparable<T> value = (entries == null) ? null : (Comparable<T>) entries.getElement();
		return value;
	}

	public static <T> Comparable<T> getHighestValue(Multiset<T> valueSet) {
		Iterable<Multiset.Entry<T>> values = MultisetUtil.getEntriesSortedByValue(valueSet);
		List<Entry<T>> entries = createEntryList(values);
		Comparable<T> value = entries.size() == 0 ? null : (Comparable<T>) entries.get(entries.size() - 1).getElement();
		return value;
	}

	public static <T> Comparable<T> getCommonestValue(Multiset<T> valueSet) {
		Iterable<Multiset.Entry<T>> values = MultisetUtil.getEntriesSortedByCount(valueSet);
		Multiset.Entry<T> entries = values.iterator().hasNext() ? (Multiset.Entry<T>) values.iterator().next() : null;
		Comparable<T> value = (entries == null) ? null : (Comparable<T>) entries.getElement();
		return value;
	}

	public static <T> List<Entry<T>> createEntryList(Iterable<Entry<T>> iterable) {
		List<Entry<T>> entries = new ArrayList<Entry<T>>();
		for (Entry<T> entry : iterable) {
			entries.add(entry);
		}
		return entries;
	}

	public static <T> List<Entry<T>> createListSortedByValue(Multiset<T> set) {
		return MultisetUtil.createEntryList(MultisetUtil.getEntriesSortedByValue(set));
	}

	public static <T> List<Entry<T>> createListSortedByCount(Multiset<T> set) {
		return MultisetUtil.createEntryList(MultisetUtil.getEntriesSortedByCount(set));
	}

	public static Map<Integer, Integer> createIntegerFrequencyMap(Multiset<Integer> set) {
		Map<Integer, Integer> countByInteger = new HashMap<Integer, Integer>();
		for (Entry<Integer> entry : set.entrySet()) {
			countByInteger.put(entry.getElement(), entry.getCount());
		}
		return countByInteger;
	}

	/** creates new list with entries whose count is at least a given value
	 * 
	 * @param entries
	 * @param minCount
	 * @return new List (empty if none)
	 */
	public static <T> List<Entry<T>> createEntriesWithCountGreater(List<Entry<T>> entries, int minCount) {
		List<Entry<T>> newEntries = new ArrayList<Entry<T>>();
		for (Entry<T> entry : entries) {
			if (entry.getCount() >= minCount) {
				newEntries.add(entry);
			}
		}
		return newEntries;
	}
	
	/** create Multset from the String representation.
	 * 
	 * @param multisetString
	 * @return
	 */
	public static Multiset<String> createMultiset(String multisetString) {
		Multiset<String> plantPartMultiset = HashMultiset.create();
		multisetString = multisetString.substring(1,  multisetString.length() - 1);
		String[] multisetStrings = multisetString.split("\\s*\\,\\s*");
		for (String s : multisetStrings) {
			String[] ss = s.split(" x ");
			String value = ss[0];
			int count = ss.length ==  1 ? 1 : Integer.parseInt(ss[1]);
			plantPartMultiset.add(value, count);
		}
		return  plantPartMultiset;
	}



	public static void writeCSV(File csvFile, List<Entry<String>> entryList, String title) throws IOException {
		if (csvFile != null) {
			List<String> rows = new ArrayList<String>();
			if (title != null) {
				rows.add(title+","+"count");
			}
			for (Entry<String> entry : entryList) {
				String element = entry.getElement();
				element = Util.escapeCSVField(element);
				rows.add(element+","+entry.getCount());
			}
			csvFile.getParentFile().mkdirs();
			FileUtils.writeLines(csvFile, rows);
		}
	}

	/** output file without title.
	 * 
	 * @param csvFile
	 * @param entryList
	 * @throws IOException
	 */
	public static void writeCSV(File csvFile, List<Entry<String>> entryList) throws IOException {
		writeCSV(csvFile, entryList, null);
	}



}
