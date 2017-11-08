package org.robinbird.graph.model;

import lombok.Getter;
import lombok.Setter;
import org.robinbird.code.model.Class;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by seokhyun on 11/8/17.
 */
@Getter
public class ClusterNode {
	@Setter private Class classeInfo;
	private ArrayList<ClusterNode> children = new ArrayList<>();

	public ClusterNode() {}
	public ClusterNode(Class classeInfo) {
		this.classeInfo = classeInfo;
	}

	public void addChild(ClusterNode n) {
		children.add(n);
	}

}
