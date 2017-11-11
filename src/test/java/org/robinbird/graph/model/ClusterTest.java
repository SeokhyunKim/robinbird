package org.robinbird.graph.model;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.robinbird.code.model.Class;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;

/**
 * Created by seokhyun on 11/11/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class ClusterTest {

	@Mock
	private ClusteringAlgorithm clusteringAlgorithm;

	@Before
	public void setup() {
		ClusterNode root = new ClusterNode();
		ClusterNode c1 = new ClusterNode();
		ClusterNode c2 = new ClusterNode();
		root.addChild(c1);
		root.addChild(c2);
		Class classMock = Mockito.mock(Class.class);
		ClusterNode c11 = new ClusterNode(classMock);
		ClusterNode c12 = new ClusterNode();
		c12.setClasseInfo(classMock);
		c1.addChild(c11);
		c1.addChild(c12);
		List<ClusterNode> roots = Arrays.asList(root);
		Mockito.when(clusteringAlgorithm.cluster(anyList())).thenReturn(roots);
	}

	@Test
	public void create_can_generate_root_list() {
		Cluster c = new Cluster(clusteringAlgorithm);
		assertNotNull(c.create(null));
	}

	@Test(expected = NullPointerException.class)
	public void when_null_algorithm_is_given_exception_is_thrown() {
		new Cluster(null);
	}

	@Test
	public void getNodesAtDepth_can_find_exact_nodes_in_given_depth() {
		Cluster c = new Cluster(clusteringAlgorithm);
		c.create(null);
		assertTrue(c.getNodesAtDepth(1).size() == 1);
		assertTrue(c.getNodesAtDepth(2).size() == 2);
		assertTrue(c.getNodesAtDepth(3).size() == 2);
	}


}
