package org.robinbird.presentation;

import static org.robinbird.model.RelationCategory.IMPLEMENTING_INTERFACE;
import static org.robinbird.model.RelationCategory.MEMBER_VARIABLE;
import static org.robinbird.model.RelationCategory.PARENT_CLASS;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import lombok.NonNull;
import org.robinbird.model.AnalysisContext;
import org.robinbird.model.Class;
import org.robinbird.model.Component;
import org.robinbird.model.ComponentCategory;
import org.robinbird.model.Relation;
import org.robinbird.model.RelationCategory;
import org.robinbird.util.StringAppender;

public class GMLPresentation implements Presentation {

    private final Set<RelationCategory> consideredCategories = ImmutableSet.of(
            PARENT_CLASS, IMPLEMENTING_INTERFACE, MEMBER_VARIABLE);

    @Override
    public String presentClasses(@NonNull final AnalysisContext analysisContext) {
        final StringAppender sa = new StringAppender();
        sa.appendLine("graph");
        sa.appendLine("[");

        // each class (or interface) becomes a node
        for(final Component component : analysisContext.getComponents(ComponentCategory.CLASS)) {
            final Class classObj = (Class) component;
            appendNodeString(sa, classObj.getId(), classObj.getName());
        }

        // relations considering inheritance, interface, and member variables
        for(final Component component : analysisContext.getComponents(ComponentCategory.CLASS)) {
            final Class classObj = (Class) component;
            classObj.getRelations().forEach((category, relations) -> {
                for (final Relation relation : relations) {
                    if (!consideredCategories.contains(relation.getRelationCategory())) {
                        continue;
                    }
                    appendEdgeString(sa, classObj.getName(), relation.getRelatedComponent().getName());
                }

            });
        }

        sa.appendLine("]");
        return sa.toString();
    }

    private void appendNodeString(final StringAppender sa, final String id, final String label) {
        sa.appendLine("node");
        sa.appendLine("[");
        sa.appendLine("\tid " + id);
        sa.appendLine("\tlabel \"" + label + "\"");
        sa.appendLine("]");
    }

    private void appendEdgeString(final StringAppender sa, final String source, final String target) {
        sa.appendLine("edge");
        sa.appendLine("[");
        sa.appendLine("source " + source);
        sa.appendLine("target " + target);
        sa.appendLine("]");
    }
}
