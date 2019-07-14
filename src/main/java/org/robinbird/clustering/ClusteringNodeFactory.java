package org.robinbird.clustering;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.robinbird.model.Component;
import org.robinbird.model.ComponentCategory;
import org.robinbird.repository.ComponentRepository;

@Slf4j
@RequiredArgsConstructor
public class ClusteringNodeFactory {

    private final static String CLUSTERING_NODE_TYPE = "clusteringNodeType";

    @NonNull
    private final ComponentRepository componentRepository;

    public AgglomerativeClusteringNode create() {
        final Component component = componentRepository.registerComponent(UUID.randomUUID().toString(), ComponentCategory.CLUSTERING_NODE);
        final Map<String, String> metadata = new HashMap<>();
        metadata.put(CLUSTERING_NODE_TYPE, AgglomerativeClusteringNode.class.getSimpleName());
        return AgglomerativeClusteringNode.builder()
                                          .id(component.getId())
                                          .name(component.getName())
                                          .metadata(metadata)
                                          .build();
    }
}
