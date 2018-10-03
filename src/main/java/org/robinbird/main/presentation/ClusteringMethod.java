package org.robinbird.main.presentation;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by seokhyun on 11/14/17.
 */
public enum ClusteringMethod {
	HIERARCHICAL_CUSTERING("hierarchy");

	private final String name;
	private static Map<String, ClusteringMethod> cmethodMap;
	static {
		cmethodMap = Arrays.stream(ClusteringMethod.values())
			.collect(Collectors.toMap(cm -> cm.getName(), cm -> cm));
	}
	ClusteringMethod(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public static ClusteringMethod getClusteringMethod(String s) {
		return cmethodMap.get(s);
	}
}
