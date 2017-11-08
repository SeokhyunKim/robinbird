package org.robinbird.graph.model;

import lombok.NonNull;
import org.robinbird.code.model.Class;
import org.robinbird.code.model.Member;
import org.robinbird.common.model.Repository;

import java.util.List;

/**
 * Created by seokhyun on 11/8/17.
 */
public class Graph {

	private Repository<Node> nodes = new Repository<>();

	public Node getNode(@NonNull final String name) {
		return nodes.getRepositable(name);
	}

	public Node getNode(int id) {
		return nodes.getRepositable(id);
	}

	public List<Node> getNodes() {
		return nodes.getRepositableList();
	}

	public int getNumNodes() {
		return nodes.size();
	}

	public Node createNode(@NonNull final String name) {
		return nodes.register(new Node(name));
	}

	public static Graph createGraphFromClasses(List<Class> classes) {
		Graph g = new Graph();
		for (Class c : classes) {
			Node n = g.createNode(c.getName());
			for (Member m : c.getMemberVariables().values()) {
				if (m.getType().isPrimitiveType()) {
					continue;
				}
				Node t = g.createNode(m.getType().getName());
				n.addEdge(t);
				t.addEdge(n);
			}
			if (c.getParent() != null) {
				Node p = g.createNode(c.getParent().getName());
				n.addEdge(p);
				p.addEdge(n);
			}
			if (c.getInterfaces().size() > 0) {
				for (Class i : c.getInterfaces()) {
					Node itf = g.createNode(i.getName());
					itf.addEdge(n);
					n.addEdge(itf);
				}
			}
		}
		return g;
	}
}
