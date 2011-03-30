package stmbench7.core;

import stmbench7.annotations.Immutable;
import stmbench7.backend.BaseIndexKey;
import stmbench7.backend.IndexKey;

/**
 * String index key type (immutable).
 */
@Immutable
public class StringIndexKey extends BaseIndexKey {
    
	private final String value;

	public StringIndexKey(String value) {
		this.value = value;
	}

	public int compareTo(IndexKey otherIndexKey) {
		StringIndexKey otherStringIndexKey = (StringIndexKey)otherIndexKey;

		return this.value.compareTo(otherStringIndexKey.value);
	}

	public String toString() {
		return value;
	}
}
