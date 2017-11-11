package org.robinbird.graph.analysis;

import org.robinbird.code.model.Class;
import org.robinbird.common.model.Pair;
import org.robinbird.graph.model.ClusterNode;
import org.robinbird.graph.model.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by seokhyun on 11/8/17.
 */
public class AgglomerativeClustering {

	private Map<String, ClusterNode> nameToClusterNodes = new HashMap<>();
	final private List<Pair<Node>> pairs;

	public AgglomerativeClustering(final List<Pair<Node>> pairs) {
		this.pairs = pairs;
	}

	public List<ClusterNode> initiate(List<Class> classes) {
		List<ClusterNode> cnodes = new ArrayList<>();
		for (Class c : classes) {
			ClusterNode cn = new ClusterNode(c);
			nameToClusterNodes.put(c.getName(), cn);
			System.out.println("new cn node: " + c.getName());
			cnodes.add(cn);
		}
		return cnodes;
	}

	public List<ClusterNode> cluster(List<ClusterNode> cnodes) {
		Set<ClusterNode> roots = new HashSet(cnodes);
		for (Pair<Node> p : pairs) {
			ClusterNode cn1 = nameToClusterNodes.get(p.getFirst().getName());
			ClusterNode cn2 = nameToClusterNodes.get(p.getSecond().getName());
			if (cn1 == null ) {
				System.out.println("not found cluster node: " + p.getFirst().getName());
				continue;
			}
			if (cn2 == null ) {
				System.out.println("not found cluster node: " + p.getSecond().getName());
				continue;
			}
			ClusterNode parent = new ClusterNode();
			parent.addChild(cn1);
			parent.addChild(cn2);
			roots.remove(cn1);
			roots.remove(cn2);
			roots.add(parent);
			nameToClusterNodes.put(p.getFirst().getName(), parent);
			nameToClusterNodes.put(p.getSecond().getName(), parent);
		}
		if (roots.size() == 1) {
			return new ArrayList<>(roots);
		}
		ClusterNode root = new ClusterNode();
		for (ClusterNode r : roots) {
			root.addChild(r);
		}
		return Arrays.asList(root);
	}

}
