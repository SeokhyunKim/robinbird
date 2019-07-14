package org.robinbird.main.newpresentation;

import lombok.extern.slf4j.Slf4j;
import org.robinbird.graph.cluster.AgglomerativeClustering;
import org.robinbird.main.oldmodel2.AnalysisContext;


@Slf4j
public class AbstractedClassesPresentation implements AnalysisContextPresentation {

	private org.robinbird.graph.cluster.ClusteringMethod clusteringMethod;
	private float score1, score2;

	public AbstractedClassesPresentation(ClusteringMethod cmethod, float score1, float score2) {
		this.clusteringMethod = createClusteringMethod(cmethod);
		this.score1 = score1;
		this.score2 = score2;
	}

	public String present(AnalysisContext analysisContext) {
		/*
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
		*/
		return "not implemented yet";
	}

	private org.robinbird.graph.cluster.ClusteringMethod createClusteringMethod(ClusteringMethod cmethod) {
		switch (cmethod) {
			case HIERARCHICAL_CUSTERING:
			default:
				return new AgglomerativeClustering();
		}
	}
}
