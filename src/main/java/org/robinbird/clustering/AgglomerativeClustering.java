package org.robinbird.clustering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.robinbird.model.Component;
import org.robinbird.util.Msgs;

@Slf4j
@AllArgsConstructor
public class AgglomerativeClustering implements ClusteringMethod {

    private static final double SCORE_MAX = 10.0;

    @NonNull
    private final ClusteringNodeFactory clusteringNodeFactory;

    private final double scoreMin, scoreMax;

    @Override
    public List<ClusteringNode> cluster(@NonNull final List<Component> components,
                                        @NonNull final RelationsSelector relationsSelector,
                                        @NonNull final ClusteringNodeMatcher nodeMatcher) {
        final Map<Long, Map<Long, NodeDistance>> dist = FloydAlgorithm.calculateDistances(components, relationsSelector);
        final Map<Long, Component> idToComps = components.stream().collect(Collectors.toMap(Component::getId, Function.identity()));
        final Map<Long, AgglomerativeClusteringNode> roots = new HashMap<>();
        final List<Edge> edges = new ArrayList<>();

        final List<Long> compIds = new ArrayList<>(dist.keySet());
        Collections.sort(compIds);
        final int size = compIds.size();
        for (int idx1=0; idx1<size; ++idx1) {
            for (int idx2=idx1+1; idx2<size; ++idx2) {
                final long i = compIds.get(idx1);
                final long j = compIds.get(idx2);
                final NodeDistance dist_ij = dist.get(i).get(j);
                if (!dist_ij.equals(NodeDistance.INFINITE)) {
                    edges.add(new Edge(idToComps.get(i), idToComps.get(j), dist_ij.getDistance()));
                }
            }
        }
        Collections.sort(edges);

        final Map<Component, AgglomerativeClusteringNode> compToClusteringNode = new HashMap<>();
        components.forEach(component -> {
            final AgglomerativeClusteringNode clusteringNode = clusteringNodeFactory.create();
            clusteringNode.addMemberNode(component);
            clusteringNode.setScore(0.0);
            compToClusteringNode.put(component, clusteringNode);
            roots.put(clusteringNode.getId(), clusteringNode);
        });

        double realMaxScore = 0.0;
        for (final Edge edge : edges) {
            final AgglomerativeClusteringNode clusteringNode1 = compToClusteringNode.get(edge.getFrom());
            final AgglomerativeClusteringNode clusteringNode2 = compToClusteringNode.get(edge.getTo());
            Validate.isTrue(clusteringNode1 != null, Msgs.get(Msgs.Key.INTERNAL_ERROR));
            Validate.isTrue(clusteringNode2 != null, Msgs.get(Msgs.Key.INTERNAL_ERROR));
            if (clusteringNode1 == clusteringNode2) {
                continue;
            }
            AgglomerativeClusteringNode clusteringNode = clusteringNodeFactory.create();
            clusteringNode.addMemberNode(clusteringNode1);
            clusteringNode.addMemberNode(clusteringNode2);
            double newScore = clusteringNode1.getScore() + clusteringNode2.getScore();
            if (edge.getWeight() != Double.MAX_VALUE) {
                newScore += edge.getWeight();
            }
            clusteringNode.setScore(newScore);
            if (newScore > realMaxScore) {
                realMaxScore = newScore;
            }
            roots.remove(clusteringNode1.getId());
            roots.remove(clusteringNode2.getId());
            roots.put(clusteringNode.getId(), clusteringNode);
            compToClusteringNode.put(edge.getFrom(), clusteringNode);
            compToClusteringNode.put(edge.getTo(), clusteringNode);
        }

        // 10 is defined as maxScore
        // adding 0.1 to make sure 10 is including everything regardless of double precision
        double adjustedMax = realMaxScore * Math.min(scoreMax, SCORE_MAX) / 10.0 + 0.1;
        double adjustedMin = Math.min(realMaxScore * Math.max(scoreMin, 0.0) / 10.0, adjustedMax);
        return nodeMatcher.match(new ArrayList<>(roots.values()),
                                 new AgglomerativeClusteringNodeMatchers.MatchScoreRangeParams(adjustedMin, adjustedMax));
    }
}
