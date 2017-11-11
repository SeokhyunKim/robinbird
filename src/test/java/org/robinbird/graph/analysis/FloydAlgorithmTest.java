package org.robinbird.graph.analysis;

import org.junit.Test;
import org.robinbird.code.model.AccessModifier;
import org.robinbird.code.model.Class;
import org.robinbird.code.model.Member;
import org.robinbird.graph.model.Graph;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;

/**
 * Created by seokhyun on 11/11/17.
 */
public class FloydAlgorithmTest {
	@Test
	public void test_Floyd_algorithm_implementation() {
		Class n1 = new Class("n1");
		Class n2 = new Class("n2");
		Class n3 = new Class("n3");
		Class n4 = new Class("n4");
		Class n5 = new Class("n5");
		n1.addMember(new Member(AccessModifier.PUBLIC, n2, "test"));
		n2.addMember(new Member(AccessModifier.PUBLIC, n3, "test"));
		n3.addMember(new Member(AccessModifier.PUBLIC, n4, "test1"));
		n3.addMember(new Member(AccessModifier.PUBLIC, n5, "test2"));
		Graph g = Graph.createGraphFromClasses(Arrays.asList(n1, n2, n3, n4, n5));
		float[][] dist = FloydAlgorithm.calculateDistances(g);
		float[][] cmp = {
			{0.0f, 1.0f, 2.0f, 3.0f, 3.0f},
			{1.0f, 0.0f, 1.0f, 2.0f, 2.0f},
			{2.0f, 1.0f, 0.0f, 1.0f, 1.0f},
			{3.0f, 2.0f, 1.0f, 0.0f, 2.0f},
			{3.0f, 2.0f, 1.0f, 2.0f, 0.0f}
		};
		for (int i=0; i<5; ++i) {
			for (int j=0; j<5; ++j) {
				assertTrue(dist[i][j] == cmp[i][j]);
			}
		}
	}
}
