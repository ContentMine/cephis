package org.contentmine.eucl.euclid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.util.MultisetUtil;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/** an array of multisets representing bins
 * supports an array of Multiset<Integer> created
 * from a single Multiset<Integer>, i.e.
 * the total set is chopped into bins
 * 
 * @author pm286
 *
 */
public class IntegerMultisetList implements List<Multiset<Integer>> {
	private static final Logger LOG = Logger.getLogger(IntegerMultisetList.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public int binsize;
	public int minval;
	public int maxval;
	public int binCount;
	private List<Multiset<Integer>> binList;
	private int startValue;
	private Multiset<Integer> rawSet;
	
	public IntegerMultisetList()  {
		getOrCreateBinList();
	}
	
	public List<Multiset<Integer>> getOrCreateBinList() {
		if (binList == null) {
			binList = new ArrayList<Multiset<Integer>>();
		}
		return binList;
	}

	@Override
	public int size() {
		return getOrCreateBinList().size();
	}

	@Override
	public boolean isEmpty() {
		return getOrCreateBinList().isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return getOrCreateBinList().contains(o);
	}

	@Override
	public Iterator<Multiset<Integer>> iterator() {
		return getOrCreateBinList().iterator();
	}

	@Override
	public Object[] toArray() {
		return getOrCreateBinList().toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return getOrCreateBinList().toArray(a);
	}

	@Override
	public boolean add(Multiset<Integer> e) {
		return getOrCreateBinList().add(e);
	}

	@Override
	public boolean remove(Object o) {
		return getOrCreateBinList().remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return getOrCreateBinList().contains(c);
	}

	@Override
	public boolean addAll(Collection<? extends Multiset<Integer>> c) {
		return getOrCreateBinList().addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends Multiset<Integer>> c) {
		return getOrCreateBinList().addAll(index, c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return getOrCreateBinList().removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return getOrCreateBinList().retainAll(c);
	}

	@Override
	public void clear() {
		getOrCreateBinList().clear();
	}

	@Override
	public Multiset<Integer> get(int index) {
		return getOrCreateBinList().get(index);
	}

	@Override
	public Multiset<Integer> set(int index, Multiset<Integer> element) {
		return getOrCreateBinList().set(index, element);
	}

	@Override
	public void add(int index, Multiset<Integer> element) {
		getOrCreateBinList().add(index, element);
	}

	@Override
	public Multiset<Integer> remove(int index) {
		return getOrCreateBinList().remove(index);
	}

	@Override
	public int indexOf(Object o) {
		return getOrCreateBinList().indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return getOrCreateBinList().lastIndexOf(o);
	}

	@Override
	public ListIterator<Multiset<Integer>> listIterator() {
		return getOrCreateBinList().listIterator();
	}

	@Override
	public ListIterator<Multiset<Integer>> listIterator(int index) {
		return getOrCreateBinList().listIterator(index);
	}

	@Override
	public List<Multiset<Integer>> subList(int fromIndex, int toIndex) {
		return getOrCreateBinList().subList(fromIndex, toIndex);
	}
	
	/** create a set of bins, each of which is a multiset.
	 * because these are integers, the bins do not lose info
	 * 
	 * @param array list of integers
	 * @param binsize size of bins
	 * @return list of bins (each is a multiset)
	 */
	public List<Multiset<Integer>> createBins(IntArray array, int binsize) {
		this.binsize = binsize;
		createLimits(array, binsize);
		binList = new ArrayList<Multiset<Integer>>();
		for (int i = 0; i < binCount; i++) {
			Multiset<Integer> bin = HashMultiset.create();
			binList.add(bin);
		}
		
		List<Integer> rawList = new ArrayList<Integer>(rawSet);
		for (int i = 0; i < rawList.size(); i++) {
			Integer ii = rawList.get(i);
			int ibin = (ii - minval + 1) / this.binsize;
			binList.get(ibin).add(ii);
		}
		return binList;

	}

	private void createLimits(IntArray array, int binsize) {
		rawSet = HashMultiset.create();
		rawSet.addAll(array.getIntegerList());
		List<Multiset.Entry<Integer>> entriesSortedByValue = MultisetUtil.createEntryList(
			MultisetUtil.getEntriesSortedByValue(rawSet));
		minval = entriesSortedByValue.get(0).getElement();
		maxval = entriesSortedByValue.get(entriesSortedByValue.size() - 1).getElement();
		startValue = getStartValue();
		calculateBinCount();  
	}

	private int calculateBinCount() {
		binCount = ((maxval - startValue ) / binsize) + 1;
		return binCount;
	}

	private int getStartValue() {
		startValue = (minval / binsize ) * binsize; // use delta granularity
		return startValue;
	}

	public void forEach(Consumer<? super Multiset<Integer>> action) {
		getOrCreateBinList().forEach(action);
	}

	public void replaceAll(UnaryOperator<Multiset<Integer>> operator) {
		getOrCreateBinList().replaceAll(operator);
	}

	public boolean removeIf(Predicate<? super Multiset<Integer>> filter) {
		return getOrCreateBinList().removeIf(filter);
	}

	public void sort(Comparator<? super Multiset<Integer>> c) {
		getOrCreateBinList().sort(c);
	}

	public boolean equals(Object o) {
		return getOrCreateBinList().equals(o);
	}

	public int hashCode() {
		return getOrCreateBinList().hashCode();
	}

	public Stream<Multiset<Integer>> stream() {
		return getOrCreateBinList().stream();
	}

	public Stream<Multiset<Integer>> parallelStream() {
		return getOrCreateBinList().parallelStream();
	}

	public Spliterator<Multiset<Integer>> spliterator() {
		return getOrCreateBinList().spliterator();
	}

	public int getBinsize() {
		return binsize;
	}

	public void setBinsize(int binsize) {
		this.binsize = binsize;
	}

	public int getMinval() {
		return minval;
	}

	public int getMaxval() {
		return maxval;
	}

	public int getBinCount() {
		return binCount;
	}

	public void setBinCount(int binCount) {
		this.binCount = binCount;
	}
		


}
