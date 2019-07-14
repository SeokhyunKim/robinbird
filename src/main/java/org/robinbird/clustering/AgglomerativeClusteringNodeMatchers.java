package org.robinbird.clustering;

import static org.robinbird.model.ComponentCategory.CLUSTERING_NODE;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import org.robinbird.model.Relation;
import org.robinbird.model.RelationCategory;

public class AgglomerativeClusteringNodeMatchers {

    @Value
    @AllArgsConstructor
    public static class MatchScoreRangeParams {
        private final double min, max;
    }

    public static List<ClusteringNode> matchScoreRange(@NonNull final Collection<ClusteringNode> nodes,
                                                                    @NonNull final Object params) {
        if (nodes.isEmpty()) {
            return Lists.newArrayList();
        }
        MatchScoreRangeParams rangeParams = (MatchScoreRangeParams) params;
        final List<ClusteringNode> matchedNodes = new ArrayList<>();
        for (final ClusteringNode node : nodes) {
            if (!(node instanceof AgglomerativeClusteringNode)) {
                continue;
            }
            final AgglomerativeClusteringNode aggNode = (AgglomerativeClusteringNode) node;
            final double nodeScore = aggNode.getScore();
            if (rangeParams.getMax() <= nodeScore && nodeScore < rangeParams.getMax()) {
                matchedNodes.add(aggNode);
                final List<ClusteringNode> childResults = matchScoreRange(node.getMemberNodes(), params);
                matchedNodes.addAll(childResults);
            }
        }
        return matchedNodes;
    }
}
