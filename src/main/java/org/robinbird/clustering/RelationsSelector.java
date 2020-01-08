package org.robinbird.clustering;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.robinbird.model.Component;
import org.robinbird.model.ComponentCategory;
import org.robinbird.model.Relation;
import org.robinbird.model.RelationCategory;

public interface RelationsSelector {

    List<Relation> getRelations(Component component);

    default Set<Component> extractRelatedComponents(Collection<Relation> relations) {
        final Set<Component> relatedComponents = new HashSet<>();
        relations.forEach(relation -> relatedComponents.addAll(extractRelatedComponents(relation)));
        return relatedComponents;
    }

    default Set<Component> extractRelatedComponents(Relation relation) {
        final Component component = relation.getRelatedComponent();
        if (component.getComponentCategory() != ComponentCategory.CONTAINER) {
            return Sets.newHashSet(component);
        }
        final Set<Component> templateTypes = new HashSet<>();
        component.getRelations()
                 .forEach(componentRelation -> {
                     if (componentRelation.getRelationCategory() == RelationCategory.TEMPLATE_TYPE) {
                         templateTypes.add(componentRelation.getRelatedComponent());
                     }
                 });
        return templateTypes;
    }

}
