package org.deuce.benchmark.intset;

import org.deuce.Atomic;

public class IntSetTreeMap extends TreeMap<Integer,Object> implements IntSet {
    // Dummy value to associate with an Object in the backing Map
    private static final Object PRESENT = new Object();

    @Atomic
    public boolean add(int value) {
	return (super.put(value, PRESENT) == null);
    }
    
    @Atomic
    public boolean remove(int value) {
	return (super.remove(value) != null);
    }
    
    @Atomic
    public boolean contains(int value) {
	return super.containsKey(value);
    }
}
