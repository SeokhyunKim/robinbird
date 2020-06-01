package org.robinbird.model;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
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

    private Container(@NonNull final String id, @NonNull final String name,
                      @Nullable final Map<String, String> metadata) {
        super(id, name, ComponentCategory.CONTAINER, (List<Relation>)null, metadata);
    }

    private void setBaseType(@NonNull final Component baseType) {
        deleteRelationByCategory(RelationCategory.CONTAINER_BASE_TYPE);
        final Relation relation = Relation.builder()
                                          .relationCategory(RelationCategory.CONTAINER_BASE_TYPE)
                                          .relatedComponent(baseType)
                                          .owner(this)
                                          .build();
        addRelation(relation);
    }

    private void setTemplateTypes(@NonNull final List<Component> components) {
        deleteRelationByCategory(RelationCategory.TEMPLATE_TYPE);
        components.forEach(c -> {
            final Relation relation = Relation.builder()
                                              .relationCategory(RelationCategory.TEMPLATE_TYPE)
                                              .relatedComponent(c)
                                              .owner(this)
                                              .build();
            addRelation(relation);
        });
    }

    public Component getBaseType() {
        final List<Relation> relations = getRelationsList(RelationCategory.CONTAINER_BASE_TYPE);
        Validate.isTrue(relations.size() == 1, Msgs.get(Msgs.Key.INTERNAL_ERROR));
        return relations.get(0).getRelatedComponent();
    }

    public List<Component> getTemplateTypes() {
        return getRelationsList(RelationCategory.TEMPLATE_TYPE).stream()
                                                               .map(Relation::getRelatedComponent)
                                                               .collect(Collectors.toList());
    }

    public static Container create(@NonNull final String id,
                                   @NonNull final Component containerBaseType,
                                   @NonNull final List<Component> templateTypes,
                                   @NonNull final Component owner) {
        final String name = createContainerName(containerBaseType.getName(), templateTypes);
        final Container container = new Container(id, name, null);
        container.setBaseType(containerBaseType);
        container.setTemplateTypes(templateTypes);
        container.setOwnerComponent(owner);
        return container;
    }

    public static Container create(@NonNull final Component component) {
        final Container container = new Container(component.getId(), component.getName(), component.getMetadata());
        Validate.isTrue(component.getOwnerComponent().isPresent(), Msgs.get(Msgs.Key.INTERNAL_ERROR));
        return container;
    }

    public static String createContainerName(@NonNull final String collectionTypeName,
                                             @NonNull final List<Component> templateTypes) {
        final StringBuilder sb = new StringBuilder();
        sb.append(collectionTypeName);
        sb.append("[");
        final Iterator<Component> itor = templateTypes.iterator();
        if (itor.hasNext()) {
            sb.append(itor.next().getName());
        }
        while (itor.hasNext()) {
            sb.append(", ").append(itor.next().getName());
        }
        sb.append("]");
        return sb.toString();
    }
}
