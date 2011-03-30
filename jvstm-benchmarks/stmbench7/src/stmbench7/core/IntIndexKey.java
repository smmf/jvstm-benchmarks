package stmbench7.core;

import stmbench7.annotations.Immutable;
import stmbench7.backend.BaseIndexKey;
import stmbench7.backend.IndexKey;

/**
 * Integer index value type (immutable).
 */
@Immutable
public class IntIndexKey extends BaseIndexKey {
    
    private final int value;

    public IntIndexKey(int value) {
    	this.value = value;
    }

    public int compareTo(IndexKey otherIndexKey) {
    	IntIndexKey otherIntIndexKey = (IntIndexKey)otherIndexKey;

    	if(this.value < otherIntIndexKey.value) return -1;
    	if(this.value > otherIntIndexKey.value) return 1;
    	return 0;
    }

    public String toString() {
    	return Integer.toString(value);
    }
}
