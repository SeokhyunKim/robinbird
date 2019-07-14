package org.robinbird.presentation;

import java.util.List;
import org.robinbird.clustering.ClusteringNode;
import org.robinbird.model.AnalysisContext;

public interface Presentation {


    default String presentClasses(AnalysisContext analysisContext) {
        return "Not supported";
    }

    default String presentSequences(AnalysisContext analysisContext) {
        return "Not supported";
    }

    default String presentClusteringNodes(List<ClusteringNode> clusteringNodes) {
        return "Not supported";
    }

}
