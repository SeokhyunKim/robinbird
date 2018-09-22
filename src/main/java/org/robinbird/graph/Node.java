package org.robinbird.graph;

import lombok.Getter;
import org.robinbird.common.dao.RobinBirdObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by seokhyun on 11/7/17.
 */
@Getter
public class Node extends RobinBirdObject {

	private List<Edge> edges = new ArrayList<>();
	private List<Edge> parents = new ArrayList<>();
	private List<Edge> interfaces = new ArrayList<>();
	private List<Edge> dependencies = new ArrayList<>();

	public Node(final String name) {
		super(name);
	}

	public void addEdge(Node target) {
		Edge edge = new Edge(this, target);
		if (edges.stream().anyMatch(e -> e.equals(edge))) {
			return;
		}
		edges.add(edge);
	}

	public void addParent(Node target) {
		Edge edge = new Edge(this, target);
		parents.add(edge);
		addEdge(target);
	}

	public void addInterface(Node target) {
		Edge edge = new Edge(this, target);
		interfaces.add(edge);
		addEdge(target);
	}

	public void addDependency(Node target) {
		Edge edge = new Edge(this, target);
		dependencies.add(edge);
		addEdge(target);
	}
}
