package stmbench7.impl.backend;

import java.util.Iterator;
import java.util.TreeMap;

import stmbench7.backend.Index;
import stmbench7.backend.IndexKey;
import stmbench7.core.RuntimeError;

/**
 * A simple implementation of an index
 * (NOT thread-safe).
 */
public class TreeMapIndex<K extends IndexKey,V> implements Index<K,V> {

	protected TreeMap<K,V> index;
	
	public TreeMapIndex() {
		index = new TreeMap<K,V>();
	}

	public void put(K key, V value) {
		if(value == null) throw new RuntimeError("TreeMapIndex does not support null values!");
		index.put(key, value);
	}

	public V putIfAbsent(K key, V value) {
		if(value == null) throw new RuntimeError("TreeMapIndex does not support null values!");
		
		V oldVal = index.get(key);
		if(oldVal != null) return oldVal;
		
		index.put(key, value);
		return null;
	}
	
	public V get(K key) {
		return index.get(key);
	}

	public Iterable<V> getRange(K minKey, K maxKey) {
		return index.subMap(minKey, maxKey).values();
	}

	public boolean remove(K key) {
		V removedValue = index.remove(key);
		return (removedValue != null);
	}

	public Iterator<V> iterator() {
		return index.values().iterator();
	}
}
