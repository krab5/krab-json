package org.krab.json.path;

import static org.junit.Assert.*;

import org.junit.Test;
import java.util.Iterator;

public class ArrayIndicesTest {
	public static void checkSequence(Iterable<Integer> actual, int... expected) {
		Iterator<Integer> it = actual.iterator();
		for (int i : expected) {
			assertTrue("input does not have the same size", it.hasNext());
			assertEquals(i, (int) it.next());
		}
		assertFalse("input does not have the same size", it.hasNext());
	}

	@Test
	public void testExact() {
		ArrayIndices ai = new ArrayIndices(
				new ArrayIndices.Range(0)
			);
		checkSequence(ai, 0);
	}
	
	@Test
	public void testSimpleRange() {
		ArrayIndices ai = new ArrayIndices(
				new ArrayIndices.Range(4, 9)
			);
		checkSequence(ai, 4, 5, 6, 7, 8, 9);
	}
	
	@Test
	public void testDisjointRanges() {
		ArrayIndices ai = new ArrayIndices(
				new ArrayIndices.Range(1, 3),
				new ArrayIndices.Range(5),
				new ArrayIndices.Range(9, 10)
			);
		checkSequence(ai, 1, 2, 3, 5, 9, 10);
	}
	
	@Test
	public void testUnsorted() {
		ArrayIndices ai = new ArrayIndices(
				new ArrayIndices.Range(9, 10),
				new ArrayIndices.Range(1, 3),
				new ArrayIndices.Range(5)
			);
		checkSequence(ai, 1, 2, 3, 5, 9, 10);
	}
	
	@Test
	public void testUnnormalized1() {
		ArrayIndices ai = new ArrayIndices(
				new ArrayIndices.Range(1, 3),
				new ArrayIndices.Range(3, 5)
			);
		checkSequence(ai, 1, 2, 3, 4, 5);
	}
	
	@Test
	public void testUnnormalized2() {
		ArrayIndices ai = new ArrayIndices(
				new ArrayIndices.Range(1, 3),
				new ArrayIndices.Range(2, 5)
			);
		checkSequence(ai, 1, 2, 3, 4, 5);
	}
	
	@Test
	public void testUnnormalized3() {
		ArrayIndices ai = new ArrayIndices(
				new ArrayIndices.Range(3, 4),
				new ArrayIndices.Range(2, 5)
			);
		checkSequence(ai, 2, 3, 4, 5);
	}

}
