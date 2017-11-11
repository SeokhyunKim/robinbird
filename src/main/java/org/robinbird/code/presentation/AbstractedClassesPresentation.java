package org.robinbird.code.presentation;

import org.robinbird.code.model.AnalysisContext;
import org.robinbird.code.model.Relation;
import org.robinbird.common.model.Pair;
import org.robinbird.code.model.Class;
import org.robinbird.graph.analysis.AgglomerativeClustering;
import org.robinbird.graph.analysis.FloydAlgorithm;
import org.robinbird.graph.model.Cluster;
import org.robinbird.graph.model.ClusterNode;
import org.robinbird.graph.model.Graph;
import org.robinbird.graph.model.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by seokhyun on 11/8/17.
 */
public class AbstractedClassesPresentation implements AnalysisContextPresentation {

	private int depth;

	public AbstractedClassesPresentation(int depth) {
		this.depth = depth;
	}

	public String present(AnalysisContext analysisContext) {
		Graph g = Graph.createGraphFromClasses(analysisContext.getClasses());
		float[][] dist = FloydAlgorithm.calculateDistances(g);
		int N = g.getNumNodes();
		List<Pair<Node>> pairs = new ArrayList<>();
		for(int i=0; i<N; ++i) {
			for (int j=i+1; j<N; ++j) {
				if (dist[i][j] > 0 && dist[i][j]<Float.MAX_VALUE) {
					pairs.add(new Pair(g.getNode(i), g.getNode(j), dist[i][j]));
				}
			}
		}
		Collections.sort(pairs);

		return "";

		/*AgglomerativeClustering.setPairs(pairs);
		Cluster cluster = new Cluster();
		cluster.create(AgglomerativeClustering::initiate, AgglomerativeClustering::cluster, analysisContext.getClasses());
		List<ClusterNode> nodes = cluster.getNodesAtDepth(depth);
		System.out.println("nodes at depth " + depth + ": " + nodes.size());
		StringAppender sa = new StringAppender();
		nodes.forEach(n -> sa.append(printClusterNode(n)));

		// relations
		for (Relation r : analysisContext.getRelations()) {
			String firstName = removeGenerics(r.getFirst().getName());
			String secondName = removeGenerics(r.getSecond().getName());
			if (r.getFirstCardinality() == null) {
				sa.appendLine(firstName + " --> " + secondName);
			} else if (r.getSecondCardinality() == null) {
				sa.appendLine(firstName + " <-- " + secondName);
			} else {
				sa.appendLine(firstName + " -- " + secondName);
			}
		}
		sa.appendLine("@enduml");

		return sa.toString();*/
	}

	private String removeGenerics(String name) {
		if (!isGeneric(name)) { return name; }
		return name.substring(0, name.indexOf("<"));
	}

	private boolean isGeneric(String name) {
		return name.contains("<");
	}

	private String printClusterNode(ClusterNode cn) {
		List<Class> classes = new ArrayList<>();
		getAllClassesFromClusterNode(cn, classes);
		StringAppender sa = new StringAppender();
		sa.appendLine("node {");
		classes.forEach(c -> sa.appendLine("class " + c.getName() + "{}"));
		sa.appendLine("}");
		return sa.toString();
	}

	private void getAllClassesFromClusterNode(ClusterNode cn, List<Class> classes) {
		if (cn.getClasseInfo() != null) {
			classes.add(cn.getClasseInfo());
		}
		for (ClusterNode child : cn.getChildren()) {
			if (child == null) {
				System.out.println("found null child clusternode!@");
			}
			getAllClassesFromClusterNode(child, classes);
		}
	}
}
