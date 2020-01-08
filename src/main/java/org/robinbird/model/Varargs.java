package org.robinbird.model;

import static org.robinbird.util.Msgs.Key.FOUND_COMPONENT_OF_DIFFERENT_TYPE;

import lombok.Builder;
import lombok.NonNull;
import org.apache.commons.lang3.Validate;
import org.robinbird.util.Msgs;

public class Varargs extends Component {

    @Builder
    public Varargs(@NonNull final String id, @NonNull String name) {
        super(id, name, ComponentCategory.VARARGS, null, null);
    }

    public void addBaseType(@NonNull final Component type) {
        final Relation relation = Relation.builder()
                                          .parent(this)
                                          .relatedComponent(type)
                                          .relationCategory(RelationCategory.VARARGS_BASE_TYPE)
                                          .build();
        addRelation(relation);
    }

    public static Varargs create(@NonNull final Component component) {
        Validate.isTrue(component.getComponentCategory() == ComponentCategory.VARARGS,
                        Msgs.get(FOUND_COMPONENT_OF_DIFFERENT_TYPE, component.getName(), component.getComponentCategory().name()));
        return Varargs.builder()
                      .id(component.getId())
                      .name(component.getName())
                      .build();
    }
}