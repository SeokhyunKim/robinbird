package org.robinbird.clustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.NonNull;
import org.robinbird.model.Component;
import org.robinbird.model.Relation;

public class FloydAlgorithm {

    public static Map<Long, Map<Long, NodeDistance>> calculateDistances(@NonNull final List<Component> nodes,
                                                                        @NonNull final RelationsSelector relationsSelector) {
        final Map<Long, Map<Long, NodeDistance>> dist = new HashMap<>();
        final List<Long> allIds = new ArrayList<>(nodes.size());
        double scoreSum = 0.0;
        for (final Component node1 : nodes) {
            allIds.add(node1.getId());
            scoreSum += relationsSelector.getRelations(node1).size();
        }
        final double totalRelationScore = scoreSum;
        for (final Component node1 : nodes) {
            for (final Component node2 : nodes) {
                dist.computeIfAbsent(node1.getId(), k -> new HashMap<>())
                    .put(node2.getId(), node1.getId() == node2.getId() ? NodeDistance.ZERO : NodeDistance.INFINITE);
            }
        }
        for (final Component node : nodes) {
            List<Relation> relations = relationsSelector.getRelations(node);
            Map<Component, List<Relation>> relatedNodeToRelations = new HashMap<>();
            relations.forEach(relation -> relatedNodeToRelations.computeIfAbsent(relation.getRelatedComponent(), k -> new ArrayList<>())
                                                                .add(relation));
            relatedNodeToRelations.forEach((relatedNode, relatedNodeRelations) -> {
                if (relatedNode.getId() != node.getId()) {
                    double relatedScore = relatedNodeRelations.size();
                    dist.get(node.getId())
                        .compute(relatedNode.getId(), (k, oldVal) -> {
                            if (oldVal != null && oldVal.equals(NodeDistance.INFINITE)) {
                                return new NodeDistance(relatedScore / totalRelationScore);
                            }
                            return Optional.ofNullable(oldVal).orElse(NodeDistance.ZERO).plus(relatedScore / totalRelationScore);
                        });
                    if (allIds.contains(relatedNode.getId())) {
                        dist.get(relatedNode.getId())
                            .compute(relatedNode.getId(), (k, oldVal) -> {
                                if (oldVal != null && oldVal.equals(NodeDistance.INFINITE)) {
                                    return new NodeDistance(relatedScore / totalRelationScore);
                                }
                                return Optional.ofNullable(oldVal).orElse(NodeDistance.ZERO).plus(relatedScore / totalRelationScore);
                            });
                    }
                }
            });
        }
        for (final Component node1 : nodes) {
            for (final Component node2 : nodes) {
                if (node1.getId() == node2.getId()) {
                    continue;
                }
                dist.get(node1.getId()).compute(node2.getId(), (k, score) -> {
                    if (score != null && !score.equals(NodeDistance.INFINITE)) {
                        return new NodeDistance(1.0 / score.getDistance());
                    }
                    return score;
                });
            }
        }
        for (long k : allIds) {
            for (long i : allIds) {
                for (long j : allIds) {
                    NodeDistance dist_ik = dist.get(i).get(k);
                    NodeDistance dist_kj = dist.get(k).get(j);
                    if (dist_ik.equals(NodeDistance.INFINITE) || dist_kj.equals(NodeDistance.INFINITE)) {
                        continue;
                    }
                    NodeDistance dist_ij = dist.get(i).get(j);
                    if (dist_ij.greaterThan(dist_ik, dist_kj)) {
                        dist.get(i).put(j, dist_ik.plus(dist_kj));
                    }
                }
            }
        }
        return dist;
    }
}
