package org.robinbird.model;

import static org.robinbird.util.Msgs.Key.FOUND_COMPONENT_OF_DIFFERENT_TYPE;

import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.apache.commons.lang3.Validate;
import org.robinbird.util.Msgs;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Collection extends Component {

    @Builder
    public Collection(final long id, @NonNull final String name, @Nullable final List<Relation> relatedTypes) {
        super(id, name, ComponentCategory.COLLECTION, relatedTypes, null);
    }

    public void addRelatedTypes(@NonNull final List<Component> relatedTypes) {
        final List<Relation> relations = relatedTypes.stream()
                                                     .map(c -> Relation.builder()
                                                                       .parent(this)
                                                                       .relationCategory(RelationCategory.COLLECTION_ELEMENT_TYPE)
                                                                       .relatedComponent(c)
                                                                       .build())
                                                     .collect(Collectors.toList());
        relations.forEach(this::addRelation);
    }

    public static Collection create(@NonNull final Component component) {
        Validate.isTrue(component.getComponentCategory() == ComponentCategory.COLLECTION,
                        Msgs.get(FOUND_COMPONENT_OF_DIFFERENT_TYPE, component.getName(), component.getComponentCategory().name()));
        return Collection.builder()
                         .id(component.getId())
                         .name(component.getName())
                         .relatedTypes(component.getRelations())
                         .build();
    }

}
