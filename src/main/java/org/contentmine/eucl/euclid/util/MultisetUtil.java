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

	/** sort entrySet by count.
	 * convenience method.
	 * @param wordSet
	 * @return
	 */
//	public static Iterable<Multiset.Entry<String>> getStringEntriesSortedByCount(Multiset<String> wordSet) {
//		return Multisets.copyHighestCountFirst(wordSet).entrySet();
//	}
//
//	public static Iterable<Entry<String>> getStringEntriesSortedByValue(Multiset<String> wordSet) {
//		return  ImmutableSortedMultiset.copyOf(wordSet).entrySet();
//	}

//	
//	public static Iterable<Entry<Integer>> getIntegerEntriesSortedByValue(Multiset<Integer> integerSet) {
//		return  ImmutableSortedMultiset.copyOf(integerSet).entrySet();		
//	}
	
//	public static Iterable<Multiset.Entry<Integer>> getIntegerEntriesSortedByCount(Multiset<Integer> integerSet) {
//		return Multisets.copyHighestCountFirst(integerSet).entrySet();
//	}


//	public static Iterable<Entry<Double>> getDoubleEntriesSortedByValue(Multiset<Double> doubleSet) {
//		return  ImmutableSortedMultiset.copyOf(doubleSet).entrySet();		
//	}
	
	public static <T> Iterable<Entry<T>> getEntriesSortedByValue(Multiset<T> set) {
		return  ImmutableSortedMultiset.copyOf(set).entrySet();		
	}
	
//	public static Iterable<Multiset.Entry<Double>> getDoubleEntriesSortedByCount(Multiset<Double> doubleSet) {
//		return Multisets.copyHighestCountFirst(doubleSet).entrySet();
//	}

	public static <T> Iterable<Multiset.Entry<T>> getEntriesSortedByCount(Multiset<T> objectSet) {
		return Multisets.copyHighestCountFirst(objectSet).entrySet();
	}

//	public static Iterable<Multiset.Entry<Int2Range>> getInt2RangeEntriesSortedByCount(Multiset<Int2Range> int2RangeSet) {
//		return Multisets.copyHighestCountFirst(int2RangeSet).entrySet();
//	}





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

//	public static Double getHighestValue(Multiset<Double> valueSet) {
//		Iterable<Multiset.Entry<Double>> values = MultisetUtil.getDoubleEntriesSortedByValue(valueSet);
//		List<Entry<Double>> entries = createDoubleEntryList(values);
//		return entries.size() == 0 ? null : entries.get(entries.size() - 1).getElement();
//	}
	
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

//	public static List<Entry<String>> createStringEntryList(Iterable<Entry<String>> iterable) {
//		List<Entry<String>> entries = new ArrayList<Entry<String>>();
//		for (Entry<String> entry : iterable) {
//			entries.add(entry);
//		}
//		return entries;
//	}
	
//	@Deprecated
//	public static List<Entry<Double>> createDoubleEntryList(Iterable<Entry<Double>> iterable) {
//		List<Entry<Double>> entries = new ArrayList<Entry<Double>>();
//		for (Entry<Double> entry : iterable) {
//			entries.add(entry);
//		}
//		return entries;
//	}

//	public static List<Entry<Integer>> createIntegerEntryList(Iterable<Entry<Integer>> iterable) {
//		List<Entry<Integer>> entries = new ArrayList<Entry<Integer>>();
//		for (Entry<Integer> entry : iterable) {
//			entries.add(entry);
//		}
//		return entries;
//	}

//	/** gets list of iterables.
//	 * 
//	 * @param iterable
//	 * @return
//	 */
//	@Deprecated
//	public static List<Entry<Object>> createObjectEntryList(Iterable<Entry<Object>> iterable) {
//		List<Entry<Object>> entries = new ArrayList<Entry<Object>>();
//		for (Entry<Object> entry : iterable) {
//			entries.add(entry);
//		}
//		return entries;
//	}

	/** gets list of iterables.
	 * 
	 * @param iterable
	 * @return
	 */
//	public static List<Entry<Int2Range>> createInt2RangeEntryList(Iterable<Entry<Int2Range>> iterable) {
//		List<Entry<Int2Range>> entries = new ArrayList<Entry<Int2Range>>();
//		for (Entry<Int2Range> entry : iterable) {
//			entries.add(entry);
//		}
//		return entries;
//	}

	public static <T> List<Entry<T>> createEntryList(Iterable<Entry<T>> iterable) {
		List<Entry<T>> entries = new ArrayList<Entry<T>>();
		for (Entry<T> entry : iterable) {
			entries.add(entry);
		}
		return entries;
	}

//	public static List<Entry<Integer>> createIntegerListSortedByCount(Multiset<Integer> lengthSet) {
//		return MultisetUtil.createEntryList(MultisetUtil.getIntegerEntriesSortedByCount(lengthSet));
//	}

	public static <T> List<Entry<T>> createListSortedByValue(Multiset<T> lengthSet) {
		return MultisetUtil.createEntryList(MultisetUtil.getEntriesSortedByValue(lengthSet));
	}

//	public static List<Entry<Double>> createDoubleListSortedByCount(Multiset<Double> lengthSet) {
//		return MultisetUtil.createEntryList(MultisetUtil.getDoubleEntriesSortedByCount(lengthSet));
//	}
//
//	public static List<Entry<Double>> createDoubleListSortedByValue(Multiset<Double> lengthSet) {
//		return MultisetUtil.createEntryList(MultisetUtil.getDoubleEntriesSortedByValue(lengthSet));
//	}
//
//	public static List<Entry<String>> createStringListSortedByCount(Multiset<String> lengthSet) {
//		return MultisetUtil.createEntryList(MultisetUtil.getStringEntriesSortedByCount(lengthSet));
//	}
//
//	public static List<Entry<String>> createStringListSortedByValue(Multiset<String> lengthSet) {
//		return MultisetUtil.createEntryList(MultisetUtil.getStringEntriesSortedByValue(lengthSet));
//	}
//	
//	public static List<Entry<Object>> createObjectListSortedByCount(Multiset<Object> objectSet) {
//		return MultisetUtil.createEntryList(MultisetUtil.getEntriesSortedByCount(objectSet));
//	}

//	public static List<Entry<Int2Range>> createInt2RangeListSortedByCount(Multiset<Int2Range> int2RangeSet) {
//	return MultisetUtil.createInt2RangeEntryList(MultisetUtil.getEntriesSortedByCount(int2RangeSet));
//}

	public static <T> List<Entry<T>> createListSortedByCount(Multiset<T> int2RangeSet) {
	return MultisetUtil.createEntryList(MultisetUtil.getEntriesSortedByCount(int2RangeSet));
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
