package stmbench7.backend;

import stmbench7.annotations.Immutable;

/**
 * Value of an index key (Index). Could have been just the
 * Comparable interface, but a.greaterThan(b) is much more
 * clear than (a.compareTo(b) > 0). Indexes (key values) are 
 * always immutable, so they do not have to be synchronized.
 */
@Immutable
public interface IndexKey extends Comparable<IndexKey> {

    public boolean greaterThan(IndexKey otherIndexKey);
    public boolean lowerThan(IndexKey otherIndexKey);
    public boolean equals(IndexKey otherIndexKey);
}
