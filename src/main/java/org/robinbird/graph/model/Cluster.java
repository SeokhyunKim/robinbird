package org.robinbird.graph.model;

import lombok.Getter;
import lombok.NonNull;
import org.robinbird.code.model.Class;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by seokhyun on 11/8/17.
 */
@Getter
public class Cluster {

	private List<ClusterNode> roots;
	private ClusteringAlgorithm clusteringAlgorithm;

	public Cluster(@NonNull final ClusteringAlgorithm clusteringAlgorithm) {
		this.clusteringAlgorithm = clusteringAlgorithm;
	}

	public List<ClusterNode> create(List<Class> classes) {
		List<ClusterNode> leaves = clusteringAlgorithm.initiate(classes);
		roots = clusteringAlgorithm.cluster(leaves);
		return roots;
	}

	public List<ClusterNode> getNodesAtDepth(int depth) {
		List<ClusterNode> nodes = new ArrayList<>();
		roots.forEach(root -> {
			getNodesAtDepthHelper(root, 1, depth, nodes);
		});
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
