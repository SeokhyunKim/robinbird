package org.robinbird.model;

import static org.robinbird.util.Msgs.Key.FOUND_COMPONENT_OF_DIFFERENT_TYPE;

import lombok.Builder;
import lombok.NonNull;
import org.apache.commons.lang3.Validate;
import org.robinbird.util.Msgs;

public class Array extends Component {

    @Builder
    public Array(final long id, @NonNull final String name) {
        super(id, name, ComponentCategory.ARRAY, null, null);
    }

    public void addBaseType(@NonNull final Component type) {
        final Relation relation = Relation.builder()
                                          .parent(this)
                                          .relatedComponent(type)
                                          .relationCategory(RelationCategory.ARRAY_BASE_TYPE)
                                          .build();
        addRelation(relation);
    }

    public static Array create(@NonNull final Component component) {
        Validate.isTrue(component.getComponentCategory() == ComponentCategory.ARRAY,
                        Msgs.get(FOUND_COMPONENT_OF_DIFFERENT_TYPE, component.getName(), component.getComponentCategory().name()));
        return Array.builder()
                         .id(component.getId())
                         .name(component.getName())
                         .build();
    }

}
