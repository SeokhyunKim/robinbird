package org.robinbird.code.presentation;

import org.robinbird.code.model.AnalysisContext;
import org.robinbird.graph.Graph;
import org.robinbird.graph.cluster.AgglomerativeClustering;
import org.robinbird.graph.cluster.Cluster;
import org.robinbird.graph.cluster.ClusterNode;
import org.robinbird.graph.cluster.ClusteringMethod;

import java.util.List;

/**
 * Created by seokhyun on 11/8/17.
 */
public class AbstractedClassesPresentation implements AnalysisContextPresentation {

	private ClusteringMethod clusteringMethod;
	private float score;

	public AbstractedClassesPresentation(CLUSTERING_METHOD cmethod, float score) {
		this.clusteringMethod = createClusteringMethod(cmethod);
		this.score = score;
	}

	public String present(AnalysisContext analysisContext) {
		Graph g = Graph.createGraphFromClasses(analysisContext.getClasses());
		Cluster c = new Cluster(clusteringMethod);
		c.create(g);
		List<ClusterNode> nodes = c.findClusterNodesWithScore(score, (nd, s) -> (nd.getScore() <= s));
		System.out.println("nodes with score: " + nodes.size());
		//System.out.println(c.printClusterTrees());


		return "";
		/*Graph g = Graph.createGraphFromClasses(analysisContext.getClasses());
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

		AgglomerativeClustering clustering = new AgglomerativeClustering(pairs);
		Cluster cluster = new Cluster(clustering);
		cluster.create(analysisContext.getClasses());
		List<AggClusterNode> nodes = cluster.getNodesAtDepth(depth);
		System.out.println("nodes at depth " + depth + ": " + nodes.size());
		StringAppender sa = new StringAppender();
		//nodes.forEach(n -> sa.append(printClusterNode(n)));

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

	/*private String removeGenerics(String name) {
		if (!isGeneric(name)) { return name; }
		return name.substring(0, name.indexOf("<"));
	}

	private boolean isGeneric(String name) {
		return name.contains("<");
	}

	private String printClusterNode(AggClusterNode cn) {
		List<Class> classes = new ArrayList<>();
		getAllClassesFromClusterNode(cn, classes);
		StringAppender sa = new StringAppender();
		sa.appendLine("node {");
		classes.forEach(c -> sa.appendLine("class " + c.getName() + "{}"));
		sa.appendLine("}");
		return sa.toString();
	}

	private void getAllClassesFromClusterNode(AggClusterNode cn, List<Class> classes) {
		if (cn.getClasseInfo() != null) {
			classes.add(cn.getClasseInfo());
		}
		for (AggClusterNode child : cn.getChildren()) {
			if (child == null) {
				System.out.println("found null child clusternode!@");
			}
			getAllClassesFromClusterNode(child, classes);
		}
	}*/

	private ClusteringMethod createClusteringMethod(CLUSTERING_METHOD cmethod) {
		switch (cmethod) {
			case HIERARCHICAL_CUSTERING:
			default:
				return new AgglomerativeClustering();
		}
	}
}
