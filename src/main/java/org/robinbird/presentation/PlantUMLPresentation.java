package org.robinbird.presentation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.NonNull;
import org.apache.commons.lang3.Validate;
import org.robinbird.exception.RobinbirdException;
import org.robinbird.model.AccessLevel;
import org.robinbird.model.AnalysisContext;
import org.robinbird.model.Cardinality;
import org.robinbird.model.Class;
import org.robinbird.model.Component;
import org.robinbird.model.ComponentCategory;
import org.robinbird.model.Function;
import org.robinbird.model.Package;
import org.robinbird.model.Relation;
import org.robinbird.model.RelationCategory;
import org.robinbird.util.Msgs;
import org.robinbird.util.StringAppender;

public class PlantUMLPresentation implements Presentation {

    @Override
    public String presentClasses(@NonNull final AnalysisContext analysisContext) {
        StringAppender sa = new StringAppender();
        sa.appendLine("@startuml");
        sa.appendLine("left to right direction");
        for (Component component : analysisContext.getComponents(ComponentCategory.PACKAGE)) {
            Package classPackage = (Package) component;
            sa.appendLine("package " + classPackage.getName() + " {");
            for (Class classObj : classPackage.getClasses()) {
                // class name, member variables, and member functions
                sa.appendLine(String.format("class %s {", classObj.getName()));
                sa.append(printMemberVariables(classObj.getMemberVariableRelations()));
                sa.append(printMemberFunctions(classObj.getMemberFunctionRelations()));
                sa.appendLine("}");
            }
            sa.appendLine("}");
        }
        // inheritance
        for (Component component : analysisContext.getComponents(ComponentCategory.PACKAGE)) {
            Package classPackage = (Package) component;
            for (Class classObj : classPackage.getClasses()) {
                final Optional<Class> parentOpt = classObj.getParent();
                if (parentOpt.isPresent()) {
                    final Class parent = parentOpt.get();
                    sa.appendLine(removeGenerics(parent.getName()) + " <|-- " + removeGenerics(classObj.getName()));
                }
            }
        }
        // interfaces
        for (Component component : analysisContext.getComponents(ComponentCategory.PACKAGE)) {
            Package classPackage = (Package) component;
            for (Class classObj : classPackage.getClasses()) {
                if (classObj.getInterfaces().size() > 0) {
                    for (Class interfaceOfClassObj : classObj.getInterfaces()) {
                        sa.appendLine(removeGenerics(interfaceOfClassObj.getName()) + " <|.. " + removeGenerics(classObj.getName()));
                    }
                }
            }
        }
        // generate relationships for UML
        final Map<UMLRelation.Key, UMLRelation> umlRelations = new HashMap<>();
        for (Component component : analysisContext.getComponents(ComponentCategory.CLASS)) {
            final Class classObj = (Class) component;
            final List<Relation> relations = classObj.getRelations(RelationCategory.MEMBER_VARIABLE);
            for (final Relation relation : relations) {
                final UMLRelation.Key key = UMLRelation.createKey(component, relation.getRelatedComponent());
                if (umlRelations.get(key) != null) {
                    umlRelations.get(key).addRelation(relation.getRelatedComponent());
                } else {
                    umlRelations.put(key, new UMLRelation(component, relation.getRelatedComponent()));
                }
            }
        }
        // generate PlantUML scripts for the generated relations
        for (final UMLRelation UMLRelation : umlRelations.values()) {
            final Component first = UMLRelation.getFirst();
            final Component second = UMLRelation.getSecond();
            final String firstName = removeGenerics(first.getName());
            final String secondName = removeGenerics(second.getName());
            final Optional<Cardinality> firstToSecondOpt = UMLRelation.getCardinalityFromFirstToSecond();
            final Optional<Cardinality> secondToFirstOpt = UMLRelation.getCardinalityFromSecondToFirst();
            if (firstToSecondOpt.isPresent() && !secondToFirstOpt.isPresent()) {
                final Cardinality firstToSecond = firstToSecondOpt.get();
                sa.appendLine(firstName + " --> " + attachQuotes(firstToSecond.toString()) + " " + secondName);
            } else if (!firstToSecondOpt.isPresent() && secondToFirstOpt.isPresent()) {
                final Cardinality secondToFirst = secondToFirstOpt.get();
                sa.appendLine(firstName + " " + attachQuotes(secondToFirst.toString()) + " <-- " + secondName);
            } else if (firstToSecondOpt.isPresent() && secondToFirstOpt.isPresent()) {
                final Cardinality firstToSecond = firstToSecondOpt.get();
                final Cardinality secondToFirst = secondToFirstOpt.get();
                sa.appendLine(firstName + " " + attachQuotes(secondToFirst.toString()) + " -- "
                                      + attachQuotes(firstToSecond.toString()) + " " + secondName);
            } else {
                throw new RobinbirdException(Msgs.get(Msgs.Key.INTERNAL_ERROR));
            }
        }
        sa.appendLine("@enduml");
        return sa.toString();
    }

    private String printMemberVariables(@NonNull final List<Relation> variableRelations) {
        StringAppender sa = new StringAppender();
        variableRelations.forEach(varRelation -> {
            Validate.isTrue(varRelation.getRelationCategory() == RelationCategory.MEMBER_VARIABLE,
                            Msgs.get(Msgs.Key.INTERNAL_ERROR));
            sa.append("\t").append(convertAccessLevel(varRelation.getAccessLevel()))
              .append(" ");
            sa.append(varRelation.getName()).append(" : ").appendLine(varRelation.getRelatedComponent().getName());
        });
        return sa.toString();
    }

    private String printMemberFunctions(@NonNull final List<Relation> memberFunctionRelations) {
        StringAppender sa = new StringAppender();
        memberFunctionRelations.forEach(funcRelation -> {
            Validate.isTrue(funcRelation.getRelationCategory() == RelationCategory.MEMBER_FUNCTION,
                            Msgs.get(Msgs.Key.INTERNAL_ERROR));
            Validate.isTrue(funcRelation.getRelatedComponent().getComponentCategory() == ComponentCategory.FUNCTION,
                            Msgs.get(Msgs.Key.INTERNAL_ERROR));
            sa.append("\t").append(convertAccessLevel(funcRelation.getAccessLevel())).append(" ");
            sa.append(funcRelation.getName()).append("(");
            final Function func = (Function) funcRelation.getRelatedComponent();
            final Iterator<Component> itor = func.getParameters().iterator();
            while (itor.hasNext()) {
                final Component param = itor.next();
                sa.append(param.getName());
                if (itor.hasNext()) {
                    sa.append(", ");
                }
            }
            sa.append(") : ").appendLine(func.getReturnType().getName());
        });
        return sa.toString();
    }

    private String convertAccessLevel(@NonNull final AccessLevel accessLevel) {
        final String accessStr;
        switch (accessLevel) {
            case PUBLIC:
                accessStr = "+";
                break;
            case PRIVATE:
                accessStr = "-";
                break;
            default:
            case PROTECTED:
                accessStr = "#";
                break;
        }
        return accessStr;
    }

    private String attachQuotes(String text) {
        return "\""+text+"\"";
    }

    private String removeGenerics(String name) {
        if (!isGeneric(name)) { return name; }
        return name.substring(0, name.indexOf("<"));
    }

    private boolean isGeneric(String name) {
        return name.contains("<");
    }

}
