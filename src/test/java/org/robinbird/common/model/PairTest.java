package org.robinbird.common.model;

import org.junit.Test;

import static junit.framework.Assert.assertTrue;

/**
 * Created by seokhyun on 11/10/17.
 */
public class PairTest {

	@Test
	public void test_for_pair_creation() {
		Pair<Integer> p1 = new Pair(1, 2);
		Pair<Integer> p2 = new Pair(1, 2, 3);

		assertTrue(p1.getFirst() == 1);
		assertTrue(p1.getSecond() == 2);
		assertTrue(p1.getValueForSorting() == 0.0);
		assertTrue(p2.getFirst() == 1);
		assertTrue(p2.getSecond() == 2);
		assertTrue(p2.getValueForSorting() == 3.0);

	}
}
