package org.robinbird.clustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.NonNull;
import org.apache.commons.lang3.Validate;
import org.robinbird.model.Component;
import org.robinbird.model.Relation;
import org.robinbird.util.Msgs;

public class FloydAlgorithm {

    public static Map<String, Map<String, NodeDistance>> calculateDistances(@NonNull final List<Component> nodes,
                                                                            @NonNull final RelationsSelector relationsSelector) {
        final Map<String, Map<String, NodeDistance>> dist = new HashMap<>();
        final List<String> allIds = new ArrayList<>(nodes.size());
        double scoreSum = 0.0;
        for (final Component node : nodes) {
            allIds.add(node.getId());
            scoreSum += relationsSelector.getRelations(node).size();
        }
        final double totalRelationScore = scoreSum;
        for (final Component node1 : nodes) {
            for (final Component node2 : nodes) {
                dist.computeIfAbsent(node1.getId(), k -> new HashMap<>())
                    .put(node2.getId(), node1.getId().equals(node2.getId()) ? NodeDistance.ZERO : NodeDistance.INFINITE);
            }
        }
        for (final Component node : nodes) {
            List<Relation> relations = relationsSelector.getRelations(node);
            Map<Component, List<Relation>> relatedNodeToRelations = new HashMap<>();
            relations.forEach(relation -> relatedNodeToRelations.computeIfAbsent(relation.getRelatedComponent(),
                                                                                 k -> new ArrayList<>())
                                                                .add(relation));
            relatedNodeToRelations.forEach((relatedNode, relatedNodeRelations) -> {
                if (!relatedNode.getId().equals(node.getId())) {
                    dist.get(node.getId())
                        .compute(relatedNode.getId(), (k, oldVal) -> {
                            if (oldVal != null) {
                                Validate.isTrue(oldVal.equals(NodeDistance.INFINITE),
                                                Msgs.getAndAddMessage(Msgs.Key.INTERNAL_ERROR,
                                                                      "id: " + node.getId() +
                                                                              ", relatedId: " + relatedNode.getId() +
                                                                              ", dist: " + oldVal.getDistance()));
                                return new NodeDistance(relatedNodeRelations.size() / totalRelationScore);
                            }
                            return null;
                        });
                }
            });
        }
        for (String k : allIds) {
            for (String i : allIds) {
                for (String j : allIds) {
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
