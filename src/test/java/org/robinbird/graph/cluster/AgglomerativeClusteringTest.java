package org.robinbird.graph.cluster;

import org.junit.Test;
import org.robinbird.graph.GraphTestUtils;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by seokhyun on 11/11/17.
 */
public class AgglomerativeClusteringTest {

	@Test
	public void test_clustering() {
		AgglomerativeClustering clustering = new AgglomerativeClustering();
		Cluster c = new Cluster(clustering);
		List<ClusterNode> roots = c.create(GraphTestUtils.createTestGraph());
		assertTrue(roots.size() == 1);
	}

}
