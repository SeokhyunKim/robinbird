package org.robinbird.graph.cluster;

import lombok.Getter;
import org.robinbird.main.oldoldrepository.dao.Pair;
import org.robinbird.main.util.Msgs;
import org.robinbird.graph.FloydAlgorithm;
import org.robinbird.graph.Graph;
import org.robinbird.graph.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;
import static org.robinbird.main.util.Msgs.Key.FAILED_TO_FIND_AGGCLUSTER_NODE;

/**
 * Created by seokhyun on 11/11/17.
 */
public class AgglomerativeClustering implements ClusteringMethod {

	private Map<String, ClusterNode> nameToClusterNodes;
	@Getter private ClusterNode root;

	public List<ClusterNode> cluster(Graph g) {
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

		Set<ClusterNode> roots = new HashSet();
		nameToClusterNodes = new HashMap<>();
		for (Node nd : g.getNodes()) {
			ClusterNode cn = new ClusterNode(nd, 1.0f);
			nameToClusterNodes.put(nd.getName(), cn);
			roots.add(cn);
		}

		for (Pair<Node> pair : pairs) {
			if (roots.size() <= 1) {
				break;
			}
			String firstName = pair.getFirst().getName();
			String secondName = pair.getSecond().getName();
			ClusterNode cn1 = nameToClusterNodes.get(firstName);
			ClusterNode cn2 = nameToClusterNodes.get(secondName);
			checkState(cn1 != null, Msgs.get(FAILED_TO_FIND_AGGCLUSTER_NODE, firstName));
			checkState(cn2 != null, Msgs.get(FAILED_TO_FIND_AGGCLUSTER_NODE, secondName));
			if (cn1 == cn2) {
				continue;
			}
			ClusterNode parent = new ClusterNode();
			parent.addChild(cn1);
			parent.addChild(cn2);
			parent.setScore(cn1.getScore() + cn2.getScore());
			parent.addGraphNodes(cn1);
			parent.addGraphNodes(cn2);
			roots.remove(cn1);
			roots.remove(cn2);
			roots.add(parent);
			updateNameToClusterNodes(parent.getGraphNodes(), parent);
		}

		if (roots.size() == 1) {
			root = roots.iterator().next();
			return new ArrayList<>(roots);
		}
		root = new ClusterNode();
		float rootScore = 0.0f;
		for (ClusterNode r : roots) {
			rootScore += r.getScore();
			root.addChild(r);
		}
		root.setScore(rootScore);
		return Arrays.asList(root);
	}

	private void updateNameToClusterNodes(List<Node> graphNodes, ClusterNode newClusterNode) {
		for (Node nd : graphNodes) {
			nameToClusterNodes.put(nd.getName(), newClusterNode);
		}
	}


}
