package org.robinbird.clustering;

import static org.robinbird.model.ComponentCategory.CLUSTERING_NODE;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
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

    @Override
    public List<ClusteringNode> cluster(@NonNull final List<Component> components,
                                        @NonNull final RelationsSelector relationsSelector,
                                        final double[] params) {
        final Map<String, Map<String, NodeDistance>> dist = FloydAlgorithm.calculateDistances(components, relationsSelector);
        final Map<String, Component> idToComps = components.stream().collect(Collectors.toMap(Component::getId, Function.identity()));
        final Map<String, AgglomerativeClusteringNode> roots = new HashMap<>();
        final List<BidirectionalEdge> edges = new ArrayList<>();

        final List<String> compIds = new ArrayList<>(dist.keySet());
        for (int idx1 = 0; idx1 < compIds.size() - 1; idx1++) {
            final String i = compIds.get(idx1);
            for (int idx2 = idx1 + 1; idx2 < compIds.size(); idx2++) {
                final String j = compIds.get(idx2);
                final NodeDistance dist_ij = dist.get(i).get(j);
                final NodeDistance dist_ji = dist.get(j).get(i);
                if (dist_ij.equals(NodeDistance.INFINITE) && dist_ji.equals(NodeDistance.INFINITE)) {
                    continue;
                }
                double distance = 0.0;
                if (!dist_ij.equals(NodeDistance.INFINITE)) {
                    distance += dist_ij.getDistance();
                }
                if (!dist_ji.equals(NodeDistance.INFINITE)) {
                    distance += dist_ji.getDistance();
                }
                edges.add(new BidirectionalEdge(idToComps.get(i), idToComps.get(j), distance));
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
        for (final BidirectionalEdge edge : edges) {
            if (roots.size() <= 1) {
                break;
            }
            final AgglomerativeClusteringNode clusteringNode1 = compToClusteringNode.get(edge.getComponent1());
            final AgglomerativeClusteringNode clusteringNode2 = compToClusteringNode.get(edge.getComponent2());

            Validate.isTrue(clusteringNode1 != null, Msgs.get(Msgs.Key.INTERNAL_ERROR));
            Validate.isTrue(clusteringNode2 != null, Msgs.get(Msgs.Key.INTERNAL_ERROR));
            if (clusteringNode1.equals(clusteringNode2)) {
                continue;
            }
            AgglomerativeClusteringNode clusteringNode = clusteringNodeFactory.create();
            clusteringNode.addMemberNode(clusteringNode1);
            clusteringNode.addMemberNode(clusteringNode2);
            double newScore = clusteringNode1.getScore() + clusteringNode2.getScore() + edge.getWeight();
            clusteringNode.setScore(newScore);
            if (newScore > realMaxScore) {
                realMaxScore = newScore;
            }
            roots.remove(clusteringNode1.getId());
            roots.remove(clusteringNode2.getId());
            roots.put(clusteringNode.getId(), clusteringNode);
            updateComponentToClusteringNodeMappings(compToClusteringNode, clusteringNode, clusteringNode);
        }

        // 10 is defined as maxScore
        double score = params[0];
        double range = params.length > 1 ? params[1] : 1.0;
        if (score > SCORE_MAX) {
            score = SCORE_MAX;
        } else if (score < 0.0) {
            score = 0.0;
        }
        double adjustedScore = realMaxScore * score / SCORE_MAX;
        double adjustedRange = Math.min(realMaxScore * range / SCORE_MAX, adjustedScore);
        log.info("Given score {}, real max score {}, and adjusted score {}", score, realMaxScore, adjustedScore);
        return matchScoreEqualsOrGreaterThan(roots.values(), adjustedScore, adjustedRange)
                       .stream()
                       .map(n -> (ClusteringNode)n)
                       .collect(Collectors.toList());
    }

    private void updateComponentToClusteringNodeMappings(final Map<Component, AgglomerativeClusteringNode> mappings,
                                                         final AgglomerativeClusteringNode node,
                                                         final AgglomerativeClusteringNode newClusteringNode) {
        for (Component child : node.getMemberNodes()) {
            if (child.getComponentCategory() == CLUSTERING_NODE) {
                updateComponentToClusteringNodeMappings(mappings, (AgglomerativeClusteringNode)child, newClusteringNode);
            } else {
                mappings.put(child, newClusteringNode);
            }
        }
    }

    private List<AgglomerativeClusteringNode> matchScoreEqualsOrGreaterThan(final Collection<AgglomerativeClusteringNode> nodes,
                                                                            final double score, final double range) {
        if (nodes.isEmpty()) {
            return Lists.newArrayList();
        }
        final List<AgglomerativeClusteringNode> matchedNodes = new ArrayList<>();
        for (final AgglomerativeClusteringNode node : nodes) {
            final double nodeScore = node.getScore();
            //log.info("{}", nodeScore);
            if (Double.compare(nodeScore + range, score) < 0) {
                continue;
            }
            //log.debug("node name: {}, node score: {}", node.getName(), nodeScore);
            final List<AgglomerativeClusteringNode> childNodes = node.getMemberNodes()
                                                                     .stream()
                                                                     .filter(n -> n instanceof AgglomerativeClusteringNode)
                                                                     .map(n -> (AgglomerativeClusteringNode)n)
                                                                     .peek(n -> log.debug("child node name: {}, child node score: {}", n.getName(), n.getScore()))
                                                                     .collect(Collectors.toList());
            final boolean allLessThanScore = childNodes.stream().anyMatch(n -> Double.compare(n.getScore() + range, score) >= 0);
            if (allLessThanScore) {
                matchedNodes.addAll(matchScoreEqualsOrGreaterThan(childNodes, score, range));
            } else {
                matchedNodes.add(node);
            }
        }
        return matchedNodes;
    }
}
