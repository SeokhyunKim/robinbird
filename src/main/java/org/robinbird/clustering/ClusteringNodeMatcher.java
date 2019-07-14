package org.robinbird.clustering;

import java.util.List;


public interface ClusteringNodeMatcher {

    List<ClusteringNode> match(List<ClusteringNode> clusteringNodes, Object params);
}
