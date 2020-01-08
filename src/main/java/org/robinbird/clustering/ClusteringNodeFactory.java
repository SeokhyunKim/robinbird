package org.robinbird.clustering;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.robinbird.model.Component;
import org.robinbird.model.ComponentCategory;

@Slf4j
@RequiredArgsConstructor
public class ClusteringNodeFactory {

    private final static String CLUSTERING_NODE_TYPE = "clusteringNodeType";

    public AgglomerativeClusteringNode create() {
        final Component component = Component.createComponentWithoutPersistence(ComponentCategory.CLUSTERING_NODE);
        final Map<String, String> metadata = new HashMap<>();
        metadata.put(CLUSTERING_NODE_TYPE, AgglomerativeClusteringNode.class.getSimpleName());
        return AgglomerativeClusteringNode.builder()
                                          .id(component.getId())
                                          .name(component.getName())
                                          .metadata(metadata)
                                          .build();
    }
}
