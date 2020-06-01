package org.robinbird.model;

import static org.robinbird.model.ModelConstants.ARRAY_SUFFIX;
import static org.robinbird.util.Msgs.Key.FOUND_COMPONENT_OF_DIFFERENT_TYPE;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import lombok.NonNull;
import org.apache.commons.lang3.Validate;
import org.robinbird.util.Msgs;

public class Array extends Component {

    private Array(@NonNull final String id, @NonNull final String name,
                  @Nullable final Map<String, String> metadata) {
        super(id, name, ComponentCategory.ARRAY, (List<Relation>)null, metadata);
    }

    private void setBaseComponent(@NonNull final Component type) {
        final Relation relation = Relation.builder()
                                          .owner(this)
                                          .relatedComponent(type)
                                          .relationCategory(RelationCategory.ARRAY_BASE_TYPE)
                                          .build();
        addRelation(relation);
    }

    public Component getBaseComponent() {
        final List<Relation> relations = getRelationsList(RelationCategory.ARRAY_BASE_TYPE);
        Validate.isTrue(relations.size() == 1, Msgs.get(Msgs.Key.INTERNAL_ERROR));
        return relations.get(0).getRelatedComponent();
    }

    public static Array create(@NonNull final String id,
                               @NonNull final Component baseType,
                               @NonNull final Component owner) {
        final Array array = new Array(id, createArrayName(baseType), null);
        array.setBaseComponent(baseType);
        array.setOwnerComponent(owner);
        return array;
    }

    public static Array create(@NonNull final Component component) {
        Validate.isTrue(component.getComponentCategory() == ComponentCategory.ARRAY,
                        Msgs.get(FOUND_COMPONENT_OF_DIFFERENT_TYPE, component.getName(), component.getComponentCategory().name()));
        return new Array(component.getId(), component.getName(), component.getMetadata());
    }

    public static String createArrayName(@NonNull final Component baseType) {
        return baseType.getName() + ARRAY_SUFFIX;
    }

}
