package stmbench7.backend;

import stmbench7.annotations.Immutable;

/**
 * Value of an index (Index) -- a base class.
 */
@Immutable
public abstract class BaseIndexKey implements IndexKey {
    
	public boolean greaterThan(IndexKey otherIndexKey) {
		return compareTo(otherIndexKey) > 0;
	}

	public boolean lowerThan(IndexKey otherIndexKey) {
		return compareTo(otherIndexKey) < 0;
	}

	public boolean equals(IndexKey otherIndexKey) {
		return compareTo(otherIndexKey) == 0;
	}

	public abstract int compareTo(IndexKey otherIndexKey); 
}
