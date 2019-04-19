package org.contentmine.eucl.euclid;

import com.google.common.collect.BoundType;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.SortedMultiset;
import com.google.common.collect.TreeMultiset;

/** represents a single bin with multiple integers
 * 
 * has a range (IntRange) which must initially be set but can be readjusted later
 * 
 * @author pm286
 *
 */
public class IntegerMultiset {

	private TreeMultiset<Integer> multiset;
	private IntRange intRange;
	
	private IntegerMultiset() {
		multiset = TreeMultiset.create();
	}

	public IntegerMultiset(IntRange intRange) {
		this();
		this.setIntRange(intRange);
	}

	public void add(Multiset<Integer> values) {
		for (Integer value : values) {
			multiset.add(value);
			intRange.add(value);
		}
	}
	
	/** removes values but does not reset intRange
	 * 
	 * @param values
	 */
	public void removeAll(Multiset<Integer> values) {
		multiset.removeAll(values);
	}
	
	public void setIntRange(IntRange intRange) {
		this.intRange = new IntRange(intRange);
	}
	
	public String toString() {
		return multiset.toString();
	}

	/** get the highest values within a tolerance of the high limit
	 * 
	 * @param delta
	 * @return
	 */
	public SortedMultiset<Integer> getHighValues(int delta) {
		if (intRange == null) {
			throw new RuntimeException("null intRange");
		}
		SortedMultiset<Integer> values = multiset.tailMultiset(intRange.getMax() - delta, BoundType.CLOSED);
		return values;
	}
	
	/** get the lowest values within a tolerance of the low limit
	 * 
	 * @param delta
	 * @return
	 */
	public SortedMultiset<Integer> getLowValues(int delta) {
		if (intRange == null) {
			throw new RuntimeException("null intRange");
		}
		SortedMultiset<Integer> values = multiset.headMultiset(intRange.getMin() + delta, BoundType.CLOSED);
		return values;
	}

	public int size() {
		return multiset == null ? 0 : multiset.size();
	}

	public void resetIntRange(IntRange intRange2) {
		this.intRange = this.intRange.not(intRange2);
	}

	public void add(Integer ii) {
		multiset.add(ii);
	}
}
