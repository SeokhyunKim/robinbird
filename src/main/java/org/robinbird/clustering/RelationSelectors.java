package org.robinbird.clustering;

import static org.robinbird.model.RelationCategory.IMPLEMENTING_INTERFACE;
import static org.robinbird.model.RelationCategory.MEMBER_VARIABLE;
import static org.robinbird.model.RelationCategory.PARENT_CLASS;

import java.util.List;
import java.util.stream.Collectors;
import org.robinbird.model.Component;
import org.robinbird.model.Relation;
import org.robinbird.model.RelationCategory;

public class RelationSelectors {

    /**
     * Relations shown in class diagram. Those are member variables, parent class, and interfaces
     * @param component
     * @return
     */
    public static List<Relation> getComponentRelations(Component component) {
        return component.getRelations()
                        .stream()
                        .filter(r -> {
                            RelationCategory category = r.getRelationCategory();
                            return category == PARENT_CLASS ||
                                   category == IMPLEMENTING_INTERFACE ||
                                   category == MEMBER_VARIABLE;
                        })
                        .collect(Collectors.toList());
    }
}
