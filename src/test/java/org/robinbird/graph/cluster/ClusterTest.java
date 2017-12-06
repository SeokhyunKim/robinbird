package org.robinbird.graph.cluster;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.robinbird.graph.Node;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

/**
 * Created by seokhyun on 11/11/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class ClusterTest {

	@Mock
	private ClusteringMethod clusteringMethod;

	@Before
	public void setup() {
		ClusterNode root = new ClusterNode();
		ClusterNode c1 = new ClusterNode();
		ClusterNode c2 = new ClusterNode();
		root.addChild(c1);
		root.addChild(c2);
		Node nodeMock = Mockito.mock(Node.class);
		Mockito.when(nodeMock.getName()).thenReturn("test");
		ClusterNode c11 = new ClusterNode(nodeMock, 1.0f);
		ClusterNode c12 = new ClusterNode(nodeMock);
		c12.setScore(1.0f);
		c1.addChild(c11);
		c1.addChild(c12);
		List<ClusterNode> roots = Arrays.asList(root);
		Mockito.when(clusteringMethod.cluster(any())).thenReturn(roots);
	}

	@Test
	public void create_can_generate_root_list() {
		Cluster c = new Cluster(clusteringMethod);
		assertNotNull(c.create(null));
	}

	@Test(expected = NullPointerException.class)
	public void when_null_algorithm_is_given_exception_is_thrown() {
		new Cluster(null);
	}

	@Test
	public void findClusterNodesWithScore_can_find_nodes() {
		Cluster c = new Cluster(clusteringMethod);
		c.create(null);
		List<ClusterNode> nodes = c.findClusterNodesWithScore(1.0f, 2.0f, ScoreMatchers::range);
		assertTrue(nodes.size() == 2);
	}

	@Test
	public void test_printClusterTrees() {
		Cluster c = new Cluster(clusteringMethod);
		c.create(null);
		String output = c.printClusterTrees();
		assertTrue(output.contains("Cluster tree -----"));
		assertTrue(2 == StringUtils.countMatches(output, "{ test }"));
		System.out.println(output);
	}
}
