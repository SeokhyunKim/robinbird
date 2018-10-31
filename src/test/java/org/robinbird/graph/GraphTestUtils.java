package org.robinbird.graph;

import org.robinbird.main.model.AccessModifier;
import org.robinbird.main.model.Class;
import org.robinbird.main.model.Member;

import java.util.Arrays;

/**
 * Created by seokhyun on 11/11/17.
 */
public class GraphTestUtils {

	public static Graph createTestGraph() {
		Class n1 = new Class("n1");
		Class n2 = new Class("n2");
		Class n3 = new Class("n3");
		Class n4 = new Class("n4");
		Class n5 = new Class("n5");
		n1.addMember(new Member(AccessModifier.PUBLIC, n2, "test"));
		n2.addMember(new Member(AccessModifier.PUBLIC, n3, "test"));
		n3.addMember(new Member(AccessModifier.PUBLIC, n4, "test1"));
		n3.addMember(new Member(AccessModifier.PUBLIC, n5, "test2"));
		return Graph.createGraphFromClasses(Arrays.asList(n1, n2, n3, n4, n5));
	}

	public static float[][] getDistancesForTestGraph() {
		float[][] cmp = {
			{0.0f, 1.0f, 2.0f, 3.0f, 3.0f},
			{1.0f, 0.0f, 1.0f, 2.0f, 2.0f},
			{2.0f, 1.0f, 0.0f, 1.0f, 1.0f},
			{3.0f, 2.0f, 1.0f, 0.0f, 2.0f},
			{3.0f, 2.0f, 1.0f, 2.0f, 0.0f}
		};
		return cmp;
	}
}
