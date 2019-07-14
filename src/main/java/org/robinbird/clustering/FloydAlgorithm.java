package org.robinbird.clustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import org.robinbird.model.Component;
import org.robinbird.model.Relation;

public class FloydAlgorithm {

    public static Map<Long, Map<Long, Double>> calculateDistances(@NonNull final List<Component> nodes,
                                                                  @NonNull final RelationsSelector relationsSelector) {
        final Map<Long, Map<Long, Double>> dist = new HashMap<>();
        final List<Long> allIds = new ArrayList<>(nodes.size());
        for (final Component node1 : nodes) {
            allIds.add(node1.getId());
            for (final Component node2 : nodes) {
                dist.computeIfAbsent(node1.getId(), k -> new HashMap<>())
                    .put(node2.getId(), node1 == node2 ? 0 : Double.MAX_VALUE);
            }
        }
        for (final Component node : nodes) {
            for (final Relation edge : relationsSelector.getEdges(node)) {
                dist.get(node.getId()).put(edge.getRelatedComponent().getId(), 1.0);
            }
        }
        for (long k : allIds) {
            for (long i : allIds) {
                for (long j : allIds) {
                    double dist_ik = dist.get(i).get(k);
                    double dist_kj = dist.get(k).get(j);
                    if (dist_ik == Double.MAX_VALUE || dist_kj == Double.MAX_VALUE) {
                        continue;
                    }
                    double dist_ij = dist.get(i).get(j);
                    if (dist_ij > dist_ik + dist_kj) {
                        dist.get(i).put(j, dist_ik + dist_kj);
                    }
                }
            }
        }
        return dist;
    }

}
