package org.robinbird.code.presentation;

import lombok.extern.slf4j.Slf4j;
import org.robinbird.code.model.AnalysisContext;
import org.robinbird.graph.Graph;
import org.robinbird.graph.cluster.AgglomerativeClustering;
import org.robinbird.graph.cluster.Cluster;
import org.robinbird.graph.cluster.ClusterNode;
import org.robinbird.graph.cluster.ClusteringMethod;
import org.robinbird.graph.cluster.ScoreMatchers;

import java.util.List;

/**
 * Created by seokhyun on 11/8/17.
 */
@Slf4j
public class AbstractedClassesPresentation implements AnalysisContextPresentation {

	private ClusteringMethod clusteringMethod;
	private float score1, score2;

	public AbstractedClassesPresentation(CLUSTERING_METHOD cmethod, float score1, float score2) {
		this.clusteringMethod = createClusteringMethod(cmethod);
		this.score1 = score1;
		this.score2 = score2;
	}

	public String present(AnalysisContext analysisContext) {
		Graph g = Graph.createGraphFromClasses(analysisContext.getClasses());
		Cluster c = new Cluster(clusteringMethod);
		c.create(g);
		List<ClusterNode> nodes = c.findClusterNodesWithScore(score1, score2, ScoreMatchers::range);
		log.debug("Given Score: {} - {}, Total Num Nodes: {}", score1, score2, nodes.size());
		StringAppender sa = new StringAppender();
		sa.appendLine("@startuml");
		sa.appendLine("left to right direction");
		nodes.forEach(node -> {
			sa.appendLine("node {");
			node.getGraphNodes().stream().forEach(gnode -> {
				if (!gnode.getName().contains("[")) {
					sa.appendLine("[ " + gnode.getName() + " ]");
				}
			});
			sa.appendLine("}");
		});
		sa.appendLine("@enduml");

		return sa.toString();
	}

	private ClusteringMethod createClusteringMethod(CLUSTERING_METHOD cmethod) {
		switch (cmethod) {
			case HIERARCHICAL_CUSTERING:
			default:
				return new AgglomerativeClustering();
		}
	}
}
