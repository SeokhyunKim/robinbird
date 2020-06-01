package org.robinbird.model;

import static org.robinbird.model.ModelConstants.VARARGS_SUFFIX;
import static org.robinbird.util.Msgs.Key.FOUND_COMPONENT_OF_DIFFERENT_TYPE;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import lombok.NonNull;
import org.apache.commons.lang3.Validate;
import org.robinbird.util.Msgs;

public class Varargs extends Component {

    private Varargs(@NonNull final String id, @NonNull final String name,
                    @Nullable final Map<String, String> metadata) {
        super(id, name, ComponentCategory.VARARGS, (List<Relation>)null, metadata);
    }

    private void setBaseComponent(@NonNull final Component type) {
        final Relation relation = Relation.builder()
                                          .owner(this)
                                          .relatedComponent(type)
                                          .relationCategory(RelationCategory.VARARGS_BASE_TYPE)
                                          .build();
        addRelation(relation);
    }

    public Component getBaseComponent() {
        final List<Relation> relations = getRelationsList(RelationCategory.VARARGS_BASE_TYPE);
        Validate.isTrue(relations.size() == 1, Msgs.get(Msgs.Key.INTERNAL_ERROR));
        return relations.get(0).getRelatedComponent();
    }

    public static Varargs create(@NonNull final String id,
                                 @NonNull final Component baseType,
                                 @NonNull final Component owner) {
        final Varargs varargsComp = new Varargs(id, createVarargsName(baseType), null);
        varargsComp.setBaseComponent(baseType);
        varargsComp.setOwnerComponent(owner);
        return varargsComp;
    }

    public static Varargs create(@NonNull final Component component) {
        Validate.isTrue(component.getComponentCategory() == ComponentCategory.VARARGS,
                        Msgs.get(FOUND_COMPONENT_OF_DIFFERENT_TYPE, component.getName(), component.getComponentCategory().name()));
        return new Varargs(component.getId(), component.getName(), component.getMetadata());
    }

    public static String createVarargsName(@NonNull final Component baseType) {
        return baseType.getName() + VARARGS_SUFFIX;
    }
}
