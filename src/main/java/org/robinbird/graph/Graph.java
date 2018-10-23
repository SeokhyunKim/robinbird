package org.robinbird.graph;

import lombok.NonNull;
import org.robinbird.main.model.Class;
import org.robinbird.main.model.Collection;
import org.robinbird.main.model.Member;
import org.robinbird.main.model.Type;
import org.robinbird.main.repository.Repository;
import org.robinbird.main.util.Msgs;

import java.util.List;

import static com.google.common.base.Preconditions.checkState;

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
				Type type = m.getType();
				Node t = null;
				if (type instanceof Class) {
					t = g.createNode(type.getName());
				} else if (type instanceof Collection) {
					Collection col = (Collection)type;
					t = g.createNode(col.getAssociatedType().getName());
				}
				checkState(t != null, Msgs.get(Msgs.Key.CANNOT_CREATE_GRAPH_NODE_FROM_TYPE, type.getName()));
				n.addDependency(t);
				t.addEdge(n);
			}
			if (c.getParent() != null) {
				Node p = g.createNode(c.getParent().getName());
				n.addParent(p);
				p.addEdge(n);
			}
			if (c.getInterfaces().size() > 0) {
				for (Class i : c.getInterfaces()) {
					Node itf = g.createNode(i.getName());
					itf.addEdge(n);
					n.addInterface(itf);
				}
			}
		}
		return g;
	}
}
