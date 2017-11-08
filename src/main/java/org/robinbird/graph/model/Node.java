package org.robinbird.graph.model;

import lombok.Getter;
import org.robinbird.common.model.Repositable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by seokhyun on 11/7/17.
 */
@Getter
public class Node extends Repositable {

	private List<Edge> edges = new ArrayList<>();

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
}
