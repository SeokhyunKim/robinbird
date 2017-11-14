package org.robinbird.code.presentation;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by seokhyun on 11/14/17.
 */
public enum CLUSTERING_METHOD {
	HIERARCHICAL_CUSTERING("hierarchy");

	private final String name;
	private static Map<String, CLUSTERING_METHOD> cmethodMap;
	static {
		cmethodMap = Arrays.stream(CLUSTERING_METHOD.values())
			.collect(Collectors.toMap(cm -> cm.getName(), cm -> cm));
	}
	CLUSTERING_METHOD(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public static CLUSTERING_METHOD getClusteringMethod(String s) {
		return cmethodMap.get(s);
	}
}
