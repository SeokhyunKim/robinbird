package org.robinbird.clustering;

import static org.robinbird.model.ComponentCategory.CLUSTERING_NODE;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import lombok.NonNull;
import org.robinbird.model.Component;
import org.robinbird.model.Relation;
import org.robinbird.model.RelationCategory;

public class ClusteringNode extends Component {

    public ClusteringNode(final long id, @NonNull final String name,
                          @Nullable final List<Relation> relations,
                          @Nullable final Map<String, String> metadata) {
        super(id, name, CLUSTERING_NODE, relations, metadata);
    }

    public void addMemberNode(@NonNull final Component node) {
        final Relation relation = Relation.builder()
                                          .relationCategory(RelationCategory.CLUSTER_MEMBER)
                                          .relatedComponent(node)
                                          .parent(this)
                                          .build();
        addRelation(relation);
    }

    public List<Component> getMemberNodes() {
        final List<Relation> relations = getRelations(RelationCategory.CLUSTER_MEMBER);
        return relations.stream()
                        .map(Relation::getRelatedComponent)
                        .collect(Collectors.toList());
    }
}
