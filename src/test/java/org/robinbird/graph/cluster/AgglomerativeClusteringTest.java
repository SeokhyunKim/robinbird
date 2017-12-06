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

	@Test
	public void test_findClusterNodes_withAgglomerativeClustering() {
		AgglomerativeClustering clustering = new AgglomerativeClustering();
		Cluster c = new Cluster(clustering);
		c.create(GraphTestUtils.createTestGraph());
		List<ClusterNode> nodes = c.findClusterNodesWithScore(1.0f, 3.5f, ScoreMatchers::range);
		System.out.println(c.printClusterTrees());
		assertTrue(nodes.size() == 3);
	}

}
