package org.robinbird.graph.cluster;

import org.robinbird.graph.Graph;

import java.util.List;

/**
 * Created by seokhyun on 11/11/17.
 */
public interface ClusteringMethod {

	List<ClusterNode> cluster(Graph g);

}
