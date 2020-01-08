package org.robinbird.clustering;

import java.util.Collection;
import java.util.List;


public interface ClusteringNodeMatcher {

    List<ClusteringNode> match(Collection<ClusteringNode> clusteringNodes, Object params);
}
