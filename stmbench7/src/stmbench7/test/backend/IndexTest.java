package stmbench7.test.backend;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.Random;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;

import stmbench7.backend.BackendFactory;
import stmbench7.backend.Index;
import stmbench7.core.IntIndexKey;
import stmbench7.impl.backend.BackendFactoryImpl;

/**
 * JUnit test for an Index class.
 */
public class IndexTest {

	/**
	 * Change the factory to test other implementation.
	 */
	protected BackendFactory backendFactory;
	
	private Index<IntIndexKey,Integer> index, emptyIndex;
	
	public IndexTest() {
		backendFactory = new BackendFactoryImpl();
	}
	
	@Before
	public void setUp() throws Exception {
		index = backendFactory.createIndex();
		emptyIndex = backendFactory.createIndex();
		
		for(int key = 0; key < 100; key++) {
			int val = key * 2;
			index.put(new IntIndexKey(key), val);
		}
		
		for(int key = 100; key >= 0; key -= 2) {
			int val = key;
			index.put(new IntIndexKey(key), val);
		}
	}

	/**
	 * Test method for {@link stmbench7.impl.backend.TreeMapIndex#put(stmbench7.backend.IndexKey, java.lang.Object)}.
	 */
	@Test
	public void testPut() {
		emptyIndex.put(new IntIndexKey(5), 10);
		emptyIndex.put(new IntIndexKey(10), 11);
		emptyIndex.put(new IntIndexKey(1), 12);
		emptyIndex.put(new IntIndexKey(7), 10);
		assertTrue(emptyIndex.get(new IntIndexKey(5)) == 10);
		assertTrue(emptyIndex.get(new IntIndexKey(10)) == 11);
		assertTrue(emptyIndex.get(new IntIndexKey(1)) == 12);
		assertTrue(emptyIndex.get(new IntIndexKey(7)) == 10);
	}

	/**
	 * Test method for {@link stmbench7.impl.backend.TreeMapIndex#putIfAbsent(stmbench7.backend.IndexKey, java.lang.Object)}.
	 */
	@Test
	public void testPutIfAbsent() {
		assertNull(index.putIfAbsent(new IntIndexKey(101), 111));
		assertNull(index.putIfAbsent(new IntIndexKey(-101), 111));
		assertTrue(index.putIfAbsent(new IntIndexKey(10), 111) == 10);
		assertTrue(index.putIfAbsent(new IntIndexKey(-101), 222) == 111);
		assertTrue(index.get(new IntIndexKey(10)) == 10);		
		assertTrue(index.get(new IntIndexKey(-101)) == 111);		
	}

	/**
	 * Test method for {@link stmbench7.impl.backend.TreeMapIndex#get(stmbench7.backend.IndexKey)}.
	 */
	@Test
	public void testGet() {
		assertNull(emptyIndex.get(new IntIndexKey(4)));
		assertNull(index.get(new IntIndexKey(101)));
		assertNull(index.get(new IntIndexKey(-101)));

		for(int key = 0; key <= 100; key += 2)
			assertTrue(index.get(new IntIndexKey(key)) == key);
		for(int key = 1; key <= 100; key += 2)
			assertTrue(index.get(new IntIndexKey(key)) == key * 2);
	}

	/**
	 * Test method for {@link stmbench7.impl.backend.TreeMapIndex#getRange(stmbench7.backend.IndexKey, stmbench7.backend.IndexKey)}.
	 */
	@Test
	public void testGetRange() {
		Iterator<Integer> range = index.getRange(new IntIndexKey(5), new IntIndexKey(9)).iterator();
		assertTrue(range.next() == 10);
		assertTrue(range.next() == 6);
		assertTrue(range.next() == 14);
		assertTrue(range.next() == 8);
		assertFalse(range.hasNext());
		
		range = index.getRange(new IntIndexKey(-5), new IntIndexKey(2)).iterator();
		assertTrue(range.next() == 0);
		assertTrue(range.next() == 2);
		assertFalse(range.hasNext());
		
		range = index.getRange(new IntIndexKey(98), new IntIndexKey(120)).iterator();
		assertTrue(range.next() == 98);
		assertTrue(range.next() == 99 * 2);
		assertTrue(range.next() == 100);
		assertFalse(range.hasNext());

		range = index.getRange(new IntIndexKey(110), new IntIndexKey(120)).iterator();
		assertFalse(range.hasNext());
		range = index.getRange(new IntIndexKey(-120), new IntIndexKey(-110)).iterator();
		assertFalse(range.hasNext());

		range = emptyIndex.getRange(new IntIndexKey(110), new IntIndexKey(120)).iterator();
		assertFalse(range.hasNext());
	}

	/**
	 * Test method for {@link stmbench7.impl.backend.TreeMapIndex#remove(stmbench7.backend.IndexKey)}.
	 */
	@Test
	public void testRemove() {
		assertFalse(emptyIndex.remove(new IntIndexKey(4)));
		assertFalse(index.remove(new IntIndexKey(200)));
		assertFalse(index.remove(new IntIndexKey(-20)));

		assertTrue(index.remove(new IntIndexKey(20)));
		assertFalse(index.remove(new IntIndexKey(20)));
		assertNull(index.get(new IntIndexKey(20)));
		
		assertTrue(index.remove(new IntIndexKey(50)));
		assertFalse(index.remove(new IntIndexKey(50)));
		assertNull(index.get(new IntIndexKey(50)));

		assertTrue(index.remove(new IntIndexKey(0)));
		assertFalse(index.remove(new IntIndexKey(0)));
		assertNull(index.get(new IntIndexKey(0)));

		assertTrue(index.remove(new IntIndexKey(100)));
		assertFalse(index.remove(new IntIndexKey(100)));
		assertNull(index.get(new IntIndexKey(100)));
	}

	/**
	 * Test method for {@link stmbench7.impl.backend.TreeMapIndex#iterator()}.
	 */
	@Test
	public void testIterator() {
		Iterator<Integer> it = emptyIndex.iterator();
		assertFalse(it.hasNext());
		
		int key = 0;
		for(int val : index) {
			if(key % 2 == 0) assertEquals(val, key);
			else assertEquals(val, key * 2);
			assertTrue(key <= 100);
			key++;
		}
		assertEquals(key, 101);
	}
	
	@Test
	public void randomTest() {
		final int N = 10000;
		long time = System.currentTimeMillis();
		
		Index<IntIndexKey,Integer> randomIndex = backendFactory.createIndex();
		TreeMap<IntIndexKey,Integer> refIndex = new TreeMap<IntIndexKey,Integer>();

		Random random = new Random();
		int max = Integer.MIN_VALUE, min = Integer.MAX_VALUE;
		for(int n = 0; n < N; n++) {
			int key = random.nextInt();
			assertEquals(randomIndex.get(new IntIndexKey(key)), refIndex.get(new IntIndexKey(key)));
			randomIndex.put(new IntIndexKey(key), key);
			assertTrue(randomIndex.get(new IntIndexKey(key)) == key);
			
			refIndex.put(new IntIndexKey(key), key);
			
			max = Math.max(max, key);
			min = Math.min(min, key);
		}

		for(int n = 0; n < N; n++) {
			int key = random.nextInt();
			Integer val = randomIndex.putIfAbsent(new IntIndexKey(key), key);
			assertEquals(refIndex.get(new IntIndexKey(key)), val);
			if(val == null) refIndex.put(new IntIndexKey(key), key);
		}

		Iterator<Integer> it = randomIndex.iterator(), refIt = refIndex.values().iterator();
		int prevVal = Integer.MIN_VALUE;
		while(it.hasNext() && refIt.hasNext()) {
			int val = it.next(), refVal = refIt.next();
			assertTrue(val > prevVal);
			assertEquals(val, refVal);
			prevVal = val;
		}
		assertFalse(it.hasNext());
		assertFalse(refIt.hasNext());
		
		it = randomIndex.getRange(new IntIndexKey(min/2), new IntIndexKey(max/2)).iterator();
		refIt = refIndex.subMap(new IntIndexKey(min/2), new IntIndexKey(max/2)).values().iterator();
		while(it.hasNext()) 
			assertEquals(it.next(), refIt.next());
		assertFalse(refIt.hasNext());
		
		for(int n = 0; n < N; n++) {
			int key = random.nextInt();
			boolean res = randomIndex.remove(new IntIndexKey(key));
			boolean refRes = ( refIndex.remove(new IntIndexKey(key)) != null ); 
			assertEquals(refRes, res);
			assertNull(randomIndex.get(new IntIndexKey(key)));
		}
		
		time = System.currentTimeMillis() - time;
		System.err.println("randomTest time: " + time + " ms");
	}
}
