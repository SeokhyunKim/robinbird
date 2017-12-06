package org.robinbird.graph.cluster;

import lombok.Getter;
import lombok.NonNull;
import org.robinbird.code.presentation.StringAppender;
import org.robinbird.graph.Graph;
import org.robinbird.graph.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by seokhyun on 11/11/17.
 */
@Getter
public class Cluster {

	private ClusteringMethod clusteringMethod;
	private List<ClusterNode> roots;

	public Cluster(@NonNull final ClusteringMethod clusteringMethod) {
		this.clusteringMethod = clusteringMethod;
	}

	public List<ClusterNode> create(Graph g) {
		roots = clusteringMethod.cluster(g);
		return roots;
	}

	public List<ClusterNode> findClusterNodesWithScore(float score1, float score2, ScoreMatch matcher) {
		List<ClusterNode> nodes = new ArrayList<>();
		roots.forEach(root -> findClusterNodesWithScoreHelper(root, score1, score2, matcher, nodes));
		return nodes;
	}

	private void findClusterNodesWithScoreHelper(ClusterNode node, float score1, float score2, ScoreMatch matcher, List<ClusterNode> nodes) {
		if (matcher.match(node, score1, score2)) {
			nodes.add(node);
			return;
		}
		if (node.getChildren() != null) {
			for (ClusterNode cn : node.getChildren()) {
				findClusterNodesWithScoreHelper(cn, score1, score2, matcher, nodes);
			}
		}
	}

	public String printClusterTrees() {
		StringAppender sa = new StringAppender();
		for (ClusterNode cnd : roots) {
			sa.appendLine("Cluster tree -----");
			printClusterTreeHelper(cnd, sa);
		}
		return sa.toString();
	}

	private void printClusterTreeHelper(ClusterNode node, StringAppender sa) {
		if (node == null) {
			return;
		}
		sa.append(Integer.toString(node.hashCode()));
		sa.append("("+Float.toString(node.getScore())+")");
		if (node.getGraphNodes().size() > 0) {
			sa.append("{ ");
			for (Node nd : node.getGraphNodes()) {
				sa.append(nd.getName() + " ");
			}
			sa.append("}");
		}
		if (node.getChildren().size() > 0) {
			sa.append(" : ");
			for (ClusterNode cnd : node.getChildren()) {
				sa.append(Integer.toString(cnd.hashCode()) + " ");
			}
		}
		sa.newLine();
		for (ClusterNode cnd : node.getChildren()) {
			printClusterTreeHelper(cnd, sa);
		}
	}
}
