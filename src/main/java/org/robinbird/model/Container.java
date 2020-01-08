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
public class Container extends Component {

    @Builder
    public Container(@NonNull final String id, @NonNull final String name, @Nullable final List<Relation> relatedTypes) {
        super(id, name, ComponentCategory.CONTAINER, relatedTypes, null);
    }

    public void addRelatedTypes(@NonNull final List<Component> relatedTypes) {
        final List<Relation> relations = relatedTypes.stream()
                                                     .map(c -> Relation.builder()
                                                                       .parent(this)
                                                                       .relationCategory(RelationCategory.TEMPLATE_TYPE)
                                                                       .relatedComponent(c)
                                                                       .build())
                                                     .collect(Collectors.toList());
        relations.forEach(this::addRelation);
    }

    public static Container create(@NonNull final Component component) {
        Validate.isTrue(component.getComponentCategory() == ComponentCategory.CONTAINER,
                        Msgs.get(FOUND_COMPONENT_OF_DIFFERENT_TYPE, component.getName(), component.getComponentCategory().name()));
        return Container.builder()
                        .id(component.getId())
                        .name(component.getName())
                        .relatedTypes(component.getRelations())
                        .build();
    }

}
