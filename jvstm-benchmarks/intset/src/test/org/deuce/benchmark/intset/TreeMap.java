/* TreeMap.java -- a class providing a basic Red-Black Tree data structure,
   mapping Object --> Object
   Copyright (C) 1998, 1999, 2000, 2001, 2002, 2004, 2005, 2006  Free Software Foundation, Inc.

This file is part of GNU Classpath.

GNU Classpath is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2, or (at your option)
any later version.

GNU Classpath is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with GNU Classpath; see the file COPYING.  If not, write to the
Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
02110-1301 USA.

Linking this library statically or dynamically with other modules is
making a combined work based on this library.  Thus, the terms and
conditions of the GNU General Public License cover the whole
combination.

As a special exception, the copyright holders of this library give you
permission to link this library with independent modules to produce an
executable, regardless of the license terms of these independent
modules, and to copy and distribute the resulting executable under
terms of your choice, provided that you also meet, for each linked
independent module, the terms and conditions of the license of that
module.  An independent module is a module which is not derived from
or based on this library.  If you modify this library, you may extend
this exception to your version of the library, but you are not
obligated to do so.  If you do not wish to do so, delete this
exception statement from your version. */


package org.deuce.benchmark.intset;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedMap;

/**
 * This class provides a red-black tree implementation of the SortedMap
 * interface.  Elements in the Map will be sorted by either a user-provided
 * Comparator object, or by the natural ordering of the keys.
 *
 * The algorithms are adopted from Corman, Leiserson, and Rivest's
 * <i>Introduction to Algorithms.</i>  TreeMap guarantees O(log n)
 * insertion and deletion of elements.  That being said, there is a large
 * enough constant coefficient in front of that "log n" (overhead involved
 * in keeping the tree balanced), that TreeMap may not be the best choice
 * for small collections. If something is already sorted, you may want to
 * just use a LinkedHashMap to maintain the order while providing O(1) access.
 *
 * TreeMap is a part of the JDK1.2 Collections API.  Null keys are allowed
 * only if a Comparator is used which can deal with them; natural ordering
 * cannot cope with null.  Null values are always allowed. Note that the
 * ordering must be <i>consistent with equals</i> to correctly implement
 * the Map interface. If this condition is violated, the map is still
 * well-behaved, but you may have suprising results when comparing it to
 * other maps.<p>
 *
 * This implementation is not synchronized. If you need to share this between
 * multiple threads, do something like:<br>
 * <code>SortedMap m
 *       = Collections.synchronizedSortedMap(new TreeMap(...));</code><p>
 *
 * The iterators are <i>fail-fast</i>, meaning that any structural
 * modification, except for <code>remove()</code> called on the iterator
 * itself, cause the iterator to throw a
 * <code>ConcurrentModificationException</code> rather than exhibit
 * non-deterministic behavior.
 *
 * @author Jon Zeppieri
 * @author Bryce McKinlay
 * @author Eric Blake (ebb9@email.byu.edu)
 * @author Andrew John Hughes (gnu_andrew@member.fsf.org)
 * @see Map
 * @see HashMap
 * @see LinkedHashMap
 * @see Comparable
 * @see Comparator
 * @see Collection
 * @see Collections#synchronizedSortedMap(SortedMap)
 * @since 1.2
 * @status updated to 1.6
 * @author Luis Gabriel Ganchinho de Pina (modified original code to strip down unnecessary operations)
 */
public class TreeMap<K extends Comparable <? super K>, V> implements Iterable<V>
{
  // Implementation note:
  // A red-black tree is a binary search tree with the additional properties
  // that all paths to a leaf node visit the same number of black nodes,
  // and no red node has red children. To avoid some null-pointer checks,
  // we use the special node nil which is always black, has no relatives,
  // and has key and value of null (but is not equal to a mapping of null).

  /**
   * Color status of a node.
   */
  private static enum Color {RED, BLACK};

  /**
   * Sentinal node, used to avoid null checks for corner cases and make the
   * delete rebalance code simpler. The rebalance code must never assign
   * the parent, left, or right of nil, but may safely reassign the color
   * to be black. This object must never be used as a key in a TreeMap, or
   * it will break bounds checking of a SubMap.
   */
  private final Node nil = new Node(null, null, Color.BLACK);
  {
	  // Nil is self-referential, so we must initialize it after creation.
//	  nil.parent = nil;
//	  nil.left = nil;
//	  nil.right = nil;
  }

  /**
   * The root node of this TreeMap.
   */
  private transient Node root = nil;

  /**
   * Class to represent an entry in the tree. Holds a single key-value pair,
   * plus pointers to parent and child nodes.
   *
   * @author Eric Blake (ebb9@email.byu.edu)
   */
  private final class Node
  {

	// All fields package visible for use by nested classes.
    /** The color of this node. */
    Color color;

    /** The left child node. */
    Node left = nil;
    /** The right child node. */
    Node right = nil;
    /** The parent node. */
    Node parent = nil;

	K key;
	V value;

    /**
     * Simple constructor.
     * @param key the key
     * @param value the value
     */
    Node(K key, V value, Color color)
    {
      this.key = key;
      this.value = value;
      this.color = color;
    }
  }

  /**
   * Instantiate a new TreeMap with no elements, using the keys' natural
   * ordering to sort. All entries in the map must have a key which implements
   * Comparable, and which are <i>mutually comparable</i>, otherwise map
   * operations may throw a {@link ClassCastException}. Attempts to use
   * a null key will throw a {@link NullPointerException}.
   *
   * @see Comparable
   */
  public TreeMap()
  {
	  /*Empty*/
  }

  /**
   * Instantiate a new TreeMap, initializing it with all of the elements in
   * the provided Map.  The elements will be sorted using the natural
   * ordering of the keys. This algorithm runs in n*log(n) time. All entries
   * in the map must have keys which implement Comparable and are mutually
   * comparable, otherwise map operations may throw a
   * {@link ClassCastException}.
   *
   * @param map a Map, whose entries will be put into this TreeMap
   * @throws ClassCastException if the keys in the provided Map are not
   *         comparable
   * @throws NullPointerException if map is null
   * @see Comparable
   */
  public TreeMap(Map<? extends K, ? extends V> map)
  {
    this();
    putAll(map);
  }

  /**
   * Clears the Map so it has no keys. This is O(1).
   */
  public void clear()
  {
	  root = nil;
  }

  /**
   * Returns true if the map contains a mapping for the given key.
   *
   * @param key the key to look for
   * @return true if the key has a mapping
   * @throws ClassCastException if key is not comparable to map elements
   * @throws NullPointerException if key is null and the comparator is not
   *         tolerant of nulls
   */
  public boolean containsKey(K key)
  {
    return getNode(key) != nil;
  }

  /**
   * Return the value in this TreeMap associated with the supplied key,
   * or <code>null</code> if the key maps to nothing.  NOTE: Since the value
   * could also be null, you must use containsKey to see if this key
   * actually maps to something.
   *
   * @param key the key for which to fetch an associated value
   * @return what the key maps to, if present
   * @throws ClassCastException if key is not comparable to elements in the map
   * @throws NullPointerException if key is null but the comparator does not
   *         tolerate nulls
   * @see #put(Object, Object)
   * @see #containsKey(Object)
   */
  public V get(K key)
  {
    // Exploit fact that nil.value == null.
    return getNode(key).value;
  }

  /**
   * Puts the supplied value into the Map, mapped by the supplied key.
   * The value may be retrieved by any object which <code>equals()</code>
   * this key. NOTE: Since the prior value could also be null, you must
   * first use containsKey if you want to see if you are replacing the
   * key's mapping.
   *
   * @param key the key used to locate the value
   * @param value the value to be stored in the Map
   * @return the prior mapping of the key, or null if there was none
   * @throws ClassCastException if key is not comparable to current map keys
   * @throws NullPointerException if key is null, but the comparator does
   *         not tolerate nulls
   * @see #get(Object)
   * @see Object#equals(Object)
   */
  public V put(K key, V value)
  {
    Node current = root;
    Node parent = nil;
    int comparison = 0;
    
    // Find new node's parent.
    while (current != nil)
    {
    	parent = current;
    	comparison = key.compareTo(current.key);
    	if (comparison > 0)
    		current = current.right;
    	else if (comparison < 0)
    		current = current.left;
    	else { // Key already in tree.
	    V ret = current.value;
	    current.value = value;
	    return ret;
	}
    }

    // Set up new node.
    Node n = new Node(key, value, Color.RED);
    n.parent = parent;

    // Insert node in tree.
    if (parent == nil)
      {
        // Special case inserting into an empty tree.
        root = n;
        return null;
      }
    if (comparison > 0)
      parent.right = n;
    else
      parent.left = n;

    // Rebalance after insert.
    insertFixup(n);
    return null;
  }

  /**
   * Copies all elements of the given map into this TreeMap.  If this map
   * already has a mapping for a key, the new mapping replaces the current
   * one.
   *
   * @param m the map to be added
   * @throws ClassCastException if a key in m is not comparable with keys
   *         in the map
   * @throws NullPointerException if a key in m is null, and the comparator
   *         does not tolerate nulls
   */
  @SuppressWarnings("unchecked")
  public void putAll(Map<? extends K, ? extends V> m)
  {
    Iterator itr = m.entrySet().iterator();
    int pos = m.size();
    while (--pos >= 0)
      {
        Map.Entry<K,V> e = (Map.Entry<K,V>) itr.next();
        put(e.getKey(), e.getValue());
      }
  }

  /**
   * Removes from the TreeMap and returns the value which is mapped by the
   * supplied key. If the key maps to nothing, then the TreeMap remains
   * unchanged, and <code>null</code> is returned. NOTE: Since the value
   * could also be null, you must use containsKey to see if you are
   * actually removing a mapping.
   *
   * @param key the key used to locate the value to remove
   * @return whatever the key mapped to, if present
   * @throws ClassCastException if key is not comparable to current map keys
   * @throws NullPointerException if key is null, but the comparator does
   *         not tolerate nulls
   */
  public V remove(K key)
  {
    Node n = getNode(key);
    if (n == nil)
      return null;
    // Note: removeNode can alter the contents of n, so save value now.
    V result = n.value;
    removeNode(n);
    return result;
  }

  /**
   * Maintain red-black balance after deleting a node.
   *
   * @param node the child of the node just deleted, possibly nil
   * @param parent the parent of the node just deleted, never nil
   */
  private void deleteFixup(Node node, Node parent)
  {
    // if (parent == nil)
    //   throw new InternalError();
    // If a black node has been removed, we need to rebalance to avoid
    // violating the "same number of black nodes on any path" rule. If
    // node is red, we can simply recolor it black and all is well.
    while (node != root && node.color == Color.BLACK)
      {
        if (node == parent.left)
          {
            // Rebalance left side.
            Node sibling = parent.right;
            // if (sibling == nil)
            //   throw new InternalError();
            if (sibling.color == Color.RED)
              {
                // Case 1: Sibling is red.
                // Recolor sibling and parent, and rotate parent left.
                sibling.color = Color.BLACK;
                parent.color = Color.RED;
                rotateLeft(parent);
                sibling = parent.right;
              }

            if (sibling.left.color == Color.BLACK && sibling.right.color == Color.BLACK)
              {
                // Case 2: Sibling has no red children.
                // Recolor sibling, and move to parent.
                sibling.color = Color.RED;
                node = parent;
                parent = parent.parent;
              }
            else
              {
                if (sibling.right.color == Color.BLACK)
                  {
                    // Case 3: Sibling has red left child.
                    // Recolor sibling and left child, rotate sibling right.
                    sibling.left.color = Color.BLACK;
                    sibling.color = Color.RED;
                    rotateRight(sibling);
                    sibling = parent.right;
                  }
                // Case 4: Sibling has red right child. Recolor sibling,
                // right child, and parent, and rotate parent left.
                sibling.color = parent.color;
                parent.color = Color.BLACK;
                sibling.right.color = Color.BLACK;
                rotateLeft(parent);
                node = root; // Finished.
              }
          }
        else
          {
            // Symmetric "mirror" of left-side case.
            Node sibling = parent.left;
            // if (sibling == nil)
            //   throw new InternalError();
            if (sibling.color == Color.RED)
              {
                // Case 1: Sibling is red.
                // Recolor sibling and parent, and rotate parent right.
                sibling.color = Color.BLACK;
                parent.color = Color.RED;
                rotateRight(parent);
                sibling = parent.left;
              }

            if (sibling.right.color == Color.BLACK && sibling.left.color == Color.BLACK)
              {
                // Case 2: Sibling has no red children.
                // Recolor sibling, and move to parent.
                sibling.color = Color.RED;
                node = parent;
                parent = parent.parent;
              }
            else
              {
                if (sibling.left.color == Color.BLACK)
                  {
                    // Case 3: Sibling has red right child.
                    // Recolor sibling and right child, rotate sibling left.
                    sibling.right.color = Color.BLACK;
                    sibling.color = Color.RED;
                    rotateLeft(sibling);
                    sibling = parent.left;
                  }
                // Case 4: Sibling has red left child. Recolor sibling,
                // left child, and parent, and rotate parent right.
                sibling.color = parent.color;
                parent.color = Color.BLACK;
                sibling.left.color = Color.BLACK;
                rotateRight(parent);
                node = root; // Finished.
              }
          }
      }
    node.color = Color.BLACK;
  }

  /**
   * Returns the first sorted node in the map, or nil if empty. Package
   * visible for use by nested classes.
   *
   * @return the first node
   */
  final Node firstNode()
  {
    // Exploit fact that nil.left == nil.
    Node node = root;
    while (node.left != nil)
      node = node.left;
    return node;
  }

  /**
   * Return the TreeMap.Node associated with key, or the nil node if no such
   * node exists in the tree. Package visible for use by nested classes.
   *
   * @param key the key to search for
   * @return the node where the key is found, or nil
   */
  final Node getNode(K key)
  {
    Node current = root;
    while (current != nil)
      {
        int comparison = key.compareTo(current.key);
        if (comparison > 0)
          current = current.right;
        else if (comparison < 0)
          current = current.left;
        else
          return current;
      }
    return current;
  }

  /**
   * Maintain red-black balance after inserting a new node.
   *
   * @param n the newly inserted node
   */
  private void insertFixup(Node n)
  {
    // Only need to rebalance when parent is a RED node, and while at least
    // 2 levels deep into the tree (ie: node has a grandparent). Remember
    // that nil.color == BLACK.
    while (n.parent.color == Color.RED && n.parent.parent != nil)
      {
        if (n.parent == n.parent.parent.left)
          {
            Node uncle = n.parent.parent.right;
            // Uncle may be nil, in which case it is BLACK.
            if (uncle.color == Color.RED)
              {
                // Case 1. Uncle is RED: Change colors of parent, uncle,
                // and grandparent, and move n to grandparent.
                n.parent.color = Color.BLACK;
                uncle.color = Color.BLACK;
                uncle.parent.color = Color.RED;
                n = uncle.parent;
              }
            else
              {
                if (n == n.parent.right)
                  {
                    // Case 2. Uncle is BLACK and x is right child.
                    // Move n to parent, and rotate n left.
                    n = n.parent;
                    rotateLeft(n);
                  }
                // Case 3. Uncle is BLACK and x is left child.
                // Recolor parent, grandparent, and rotate grandparent right.
                n.parent.color = Color.BLACK;
                n.parent.parent.color = Color.RED;
                rotateRight(n.parent.parent);
              }
          }
        else
          {
            // Mirror image of above code.
            Node uncle = n.parent.parent.left;
            // Uncle may be nil, in which case it is BLACK.
            if (uncle.color == Color.RED)
              {
                // Case 1. Uncle is RED: Change colors of parent, uncle,
                // and grandparent, and move n to grandparent.
                n.parent.color = Color.BLACK;
                uncle.color = Color.BLACK;
                uncle.parent.color = Color.RED;
                n = uncle.parent;
              }
            else
              {
                if (n == n.parent.left)
                {
                    // Case 2. Uncle is BLACK and x is left child.
                    // Move n to parent, and rotate n right.
                    n = n.parent;
                    rotateRight(n);
                  }
                // Case 3. Uncle is BLACK and x is right child.
                // Recolor parent, grandparent, and rotate grandparent left.
                n.parent.color = Color.BLACK;
                n.parent.parent.color = Color.RED;
                rotateLeft(n.parent.parent);
              }
          }
      }
    root.color = Color.BLACK;
  }

  /**
   * Remove node from tree. This will increment modCount and decrement size.
   * Node must exist in the tree. Package visible for use by nested classes.
   *
   * @param node the node to remove
   */
  final void removeNode(Node node)
  {
    Node splice;
    Node child;

    // Find splice, the node at the position to actually remove from the tree.
    if (node.left == nil)
      {
        // Node to be deleted has 0 or 1 children.
        splice = node;
        child = node.right;
      }
    else if (node.right == nil)
      {
        // Node to be deleted has 1 child.
        splice = node;
        child = node.left;
      }
    else
      {
        // Node has 2 children. Splice is node's predecessor, and we swap
        // its contents into node.
        splice = node.left;
        while (splice.right != nil)
          splice = splice.right;
        child = splice.left;
        node.key = splice.key;
        node.value = splice.value;
      }

    // Unlink splice from the tree.
    Node parent = splice.parent;
    if (child != nil)
      child.parent = parent;
    if (parent == nil)
      {
        // Special case for 0 or 1 node remaining.
        root = child;
        return;
      }
    if (splice == parent.left)
      parent.left = child;
    else
      parent.right = child;

    if (splice.color == Color.BLACK)
      deleteFixup(child, parent);
  }

  /**
   * Rotate node n to the left.
   *
   * @param node the node to rotate
   */
  private void rotateLeft(Node node)
  {
    Node child = node.right;
    // if (node == nil || child == nil)
    //   throw new InternalError();

    // Establish node.right link.
    node.right = child.left;
    if (child.left != nil)
      child.left.parent = node;

    // Establish child->parent link.
    child.parent = node.parent;
    if (node.parent != nil)
      {
        if (node == node.parent.left)
          node.parent.left = child;
        else
          node.parent.right = child;
      }
    else
      root = child;

    // Link n and child.
    child.left = node;
    node.parent = child;
  }

  /**
   * Rotate node n to the right.
   *
   * @param node the node to rotate
   */
  private void rotateRight(Node node)
  {
    Node child = node.left;
    // if (node == nil || child == nil)
    //   throw new InternalError();

    // Establish node.left link.
    node.left = child.right;
    if (child.right != nil)
      child.right.parent = node;

    // Establish child->parent link.
    child.parent = node.parent;
    if (node.parent != nil)
      {
        if (node == node.parent.right)
          node.parent.right = child;
        else
          node.parent.left = child;
      }
    else
      root = child;

    // Link n and child.
    child.right = node;
    node.parent = child;
  }

  /**
   * Return the node following the given one, or nil if there isn't one.
   * Package visible for use by nested classes.
   *
   * @param node the current node, not nil
   * @return the next node in sorted order
   */
  final Node successor(Node node)
  {
    if (node.right != nil)
      {
        node = node.right;
        while (node.left != nil)
          node = node.left;
        return node;
      }

    Node parent = node.parent;
    // Exploit fact that nil.right == nil and node is non-nil.
    while (node == parent.right)
      {
        node = parent;
        parent = parent.parent;
      }
    return parent;
  }

  /**
   * Iterate over TreeMap's entries. This implementation is parameterized
   * to give a sequential view of keys, values, or entries.
   *
   * @author Eric Blake (ebb9@email.byu.edu)
   */
  private class NodeIterator implements Iterator<Node>
  {
    /** The last Entry returned by a next() call. */
    private Node last;
    /** The next entry that should be returned by next(). */
    private Node next;

    /**
     * Construct a new TreeIterator with the supplied type.
     * @param type {@link #KEYS}, {@link #VALUES}, or {@link #ENTRIES}
     */
    NodeIterator()
    {
      this.next = firstNode();
    }

    /**
     * Returns true if the Iterator has more elements.
     * @return true if there are more elements
     */
    public boolean hasNext()
    {
      return next != nil;
    }

    /**
     * Returns the next element in the Iterator's sequential view.
     * @return the next element
     * @throws ConcurrentModificationException if the TreeMap was modified
     * @throws NoSuchElementException if there is none
     */
    public Node next()
    {
      if (next == nil)
        throw new NoSuchElementException();
      
      last = next;
      next = successor(last);

      return last;
    }

    /**
     * Removes from the backing TreeMap the last element which was fetched
     * with the <code>next()</code> method.
     * @throws ConcurrentModificationException if the TreeMap was modified
     * @throws IllegalStateException if called when there is no last element
     */
    public void remove()
    {
      if (last == null)
        throw new IllegalStateException();

      removeNode(last);
      last = null;
    }
  } // class TreeIterator
  
  private final class RangeIterator implements Iterator<V> {
	  private NodeIterator iter = new NodeIterator();
	  private final K minKey, maxKey;
	  /**
	   * Always holds the next item to be returned.
	   * Only is null when there are no more items to be returned.
	   */
	  Node nextRet = null;
	  

	public RangeIterator(K min, K max) {
		this.minKey = min;
		this.maxKey = max;
		
		//Iterate until finding the first item in range
		while(iter.hasNext()) {
			nextRet = iter.next();
			if (nextRet.key.compareTo(minKey) > 0) {
				//Past lower range bound
				if (!(nextRet.key.compareTo(maxKey) < 0)) {
					//Past upper bound too
					nextRet = null;
				}
				break;
			}
		}
	}

	@Override
	public boolean hasNext() {
		if (nextRet == null)
			return false;
		
		return true;
	}

	@Override
	public V next() {
		if (nextRet == null)
			throw new NoSuchElementException();
		
		V ret = nextRet.value;
		
		nextRet = null;
		if (iter.hasNext()){
			nextRet = iter.next();
			if (!(nextRet.key.compareTo(maxKey) < 0))
				nextRet = null;
		}
		
		return ret;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
  }
  
  
  private final class TreeIterator implements Iterator<V> {
	  NodeIterator iter = new NodeIterator();

	@Override
	public boolean hasNext() {
		return iter.hasNext();
	}

	@Override
	public V next() {
		return iter.next().value;
	}

	@Override
	public void remove() {
		iter.remove();
	}
	
  }
  
	@Override
	public Iterator<V> iterator() {
		return new TreeIterator();
	}
	
	public Iterator<V> rangeIterator(K min, K max) {
		return new RangeIterator(min,max);
	}
	
	public static void main(String[] args) {
		final int LIMIT = 1000000;
		
		TreeMap<Integer, String> index = new TreeMap<Integer, String>();
		
		long start = System.currentTimeMillis();
		for(int values[] = {0,LIMIT};values[0] <= values[1];values[0]++,values[1]--) {
			index.put(values[0], Integer.toString(values[0]));
			index.put(values[1], Integer.toString(values[1]));
		}
		
		{
			//Test iterator order
			int i=0;
			for(String val : index) {
				if (!val.equals(Integer.toString(i))) {
					throw new Error(""+val+" differs from "+i);
				}
				i++;
			}
		}
		{
			//Test range iterator order
			int i=LIMIT/4,n=3*i;
			for (Iterator<String> iterator = index.rangeIterator(i,n); iterator.hasNext();) {
				i++;
				String val = iterator.next();
				if (!val.equals(Integer.toString(i))) {
					throw new Error(""+val+" differs from "+i);
				}
			}
		}
		System.out.println(System.currentTimeMillis() - start);
	}

} // class TreeMap
