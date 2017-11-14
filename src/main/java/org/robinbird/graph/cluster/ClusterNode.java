package org.robinbird.graph.cluster;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.robinbird.graph.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by seokhyun on 11/11/17.
 */
@Getter
public class ClusterNode {
	private List<Node> graphNodes = new ArrayList<>();
	private List<ClusterNode> children = new ArrayList<>();
	@Setter private float score;

	public ClusterNode() {}
	public ClusterNode(@NonNull final Node node) {
		addGraphNode(node);
	}
	public ClusterNode(@NonNull final Node node, final float score) {
		addGraphNode(node, score);
	}

	public int addGraphNode(@NonNull final Node node) {
		graphNodes.add(node);
		return graphNodes.size();
	}

	public int addGraphNode(@NonNull final Node node, float score) {
		this.score = score;
		return addGraphNode(node);
	}

	public int addGraphNodes(@NonNull final ClusterNode cn) {
		if (this != cn) {
			graphNodes.addAll(cn.getGraphNodes());
		}
		return graphNodes.size();
	}

	public int addChild(@NonNull final ClusterNode node) {
		children.add(node);
		return children.size();
	}
}
