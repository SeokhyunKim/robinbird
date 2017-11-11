package org.robinbird.graph;

import org.junit.Test;
import org.robinbird.code.model.AccessModifier;
import org.robinbird.code.model.Class;
import org.robinbird.code.model.Member;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;

/**
 * Created by seokhyun on 11/11/17.
 */
public class FloydAlgorithmTest {
	@Test
	public void test_Floyd_algorithm_implementation() {
		Graph g = GraphTestUtils.createTestGraph();
		float[][] dist = FloydAlgorithm.calculateDistances(g);
		float[][] cmp = GraphTestUtils.getDistancesForTestGraph();
		for (int i=0; i<5; ++i) {
			for (int j=0; j<5; ++j) {
				assertTrue(dist[i][j] == cmp[i][j]);
			}
		}
	}
}
