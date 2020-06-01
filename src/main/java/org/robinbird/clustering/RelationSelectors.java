package org.robinbird.clustering;

import static org.robinbird.model.RelationCategory.IMPLEMENTING_INTERFACE;
import static org.robinbird.model.RelationCategory.MEMBER_VARIABLE;
import static org.robinbird.model.RelationCategory.PARENT_CLASS;
import static org.robinbird.model.RelationCategory.PARENT_PACKAGE;

import java.util.ArrayList;
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
        final List<Relation> relations = new ArrayList<>();
        relations.addAll(component.getRelationsList(PARENT_CLASS));
        relations.addAll(component.getRelationsList(IMPLEMENTING_INTERFACE));
        relations.addAll(component.getRelationsList(MEMBER_VARIABLE));
        return relations;
    }
}
