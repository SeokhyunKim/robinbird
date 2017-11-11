package org.robinbird.graph.model;

import org.robinbird.code.model.Class;

import java.util.List;

/**
 * Created by seokhyun on 11/11/17.
 */
public interface ClusteringAlgorithm {

	List<ClusterNode> initiate(List<Class> classes);

	List<ClusterNode> cluster(List<ClusterNode> leafNodes);
}
