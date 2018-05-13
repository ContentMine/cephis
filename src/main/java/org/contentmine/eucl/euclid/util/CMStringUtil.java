package org.contentmine.eucl.euclid.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class CMStringUtil {
	private static final Logger LOG = Logger.getLogger(CMStringUtil.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	private static Pattern NUMERIC = Pattern.compile("([^\\d]*)(\\d+)(([^\\d].*)?)");

	/** sorts unique strings by embedded integers.
	 * the rest of the string is expected to be a constant framework.
	 * e.g. foo1bar, foo11bar, foo2bar is
	 * reordered to 
	 * e.g. foo1bar, foo2bar, foo11bar
	 * 
	 * if any string does not fit the regex pattern and constantFramework returns empty list.
	 * 
	 * @param strings
	 * @return list of sorted strings (empty if any conditions are violated)
	 */
	public static List<String> sortUniqueStringsByEmbeddedIntegers(List<String> strings) {
		List<String> sortedStrings = new ArrayList<String>();
		if (strings.size() > 1) {
			Matcher matcher = NUMERIC.matcher(strings.get(0));
			matcher.matches();
			if (matcher.matches() && matcher.groupCount() == 4) {
				String first = matcher.group(1);
				String last = matcher.group(3);
				// create new Pattern
				Pattern explictPattern = Pattern.compile(first+"(\\d+)"+last);
				Map<Integer, String> stringByInteger = new HashMap<Integer, String>();
				for (String s : strings) {
					matcher = explictPattern.matcher(s);
					if (!matcher.matches() || matcher.groupCount() != 1) {
						LOG.warn(s+" does not fit pattern: "+explictPattern);
						continue;
					}
					Integer ii = new Integer(matcher.group(1));
					if (stringByInteger.keySet().contains(ii)) {
						throw new RuntimeException("Duplicate string: "+s);
					}
					stringByInteger.put(ii, s);
				}
				List<Integer> intList = new ArrayList<Integer>(stringByInteger.keySet());
				Collections.sort(intList);
				for (Integer integer : intList) {
					sortedStrings.add(stringByInteger.get(integer));
				}
			}
		}
		return sortedStrings;
	}
}
