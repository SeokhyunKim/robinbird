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
		System.out.println(c.getClusterNodesWithScore(1.0f).size());
		System.out.println(c.printClusterTrees());
		assertTrue(roots.size() == 1);
	}

}
