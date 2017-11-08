package org.robinbird.graph.model;

import lombok.Getter;
import org.robinbird.code.model.Class;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by seokhyun on 11/8/17.
 */
@Getter
public class Cluster {

	private ClusterNode root;
	private List<ClusterNode> leafNodes;

	public interface ClusterInitiator {
		List<ClusterNode> initiate(List<Class> classes);
	}

	public interface ClusteringAlgorithm {
		ClusterNode cluster(List<ClusterNode> leafNodes);
	}

	public void create(ClusterInitiator initiator, ClusteringAlgorithm algo, List<Class> classes) {
		leafNodes = initiator.initiate(classes);
		root = algo.cluster(leafNodes);
	}

	public List<ClusterNode> getNodesAtDepth(int depth) {
		List<ClusterNode> nodes = new ArrayList<>();
		getNodesAtDepthHelper(root, 1, depth, nodes);
		return nodes;
	}

	private void getNodesAtDepthHelper(ClusterNode node, int curDepth, int depth, List<ClusterNode> nodes) {
		if (depth == curDepth) {
			nodes.add(node);
			return;
		}
		for (ClusterNode cn : node.getChildren()) {
			getNodesAtDepthHelper(cn, curDepth+1, depth, nodes);
		}
	}


}
