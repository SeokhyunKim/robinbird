package org.robinbird.presentation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.lang3.Validate;
import org.robinbird.clustering.ClusteringNode;
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

@AllArgsConstructor
public class PlantUMLPresentation implements Presentation {

    @NonNull
    private final CommandLine commandLine;

    @Override
    public String presentClasses(@NonNull final AnalysisContext analysisContext) {
        final boolean skipMembers = this.commandLine.hasOption("sm");
        StringAppender sa = new StringAppender();
        sa.appendLine("@startuml");
        sa.appendLine("left to right direction");
        for (Component component : analysisContext.getComponents(ComponentCategory.PACKAGE)) {
            Package classPackage = Package.builder()
                                          .id(component.getId())
                                          .name(component.getName())
                                          .relations(component.getRelations())
                                          .build();
            sa.appendLine("package " + classPackage.getName() + " {");
            for (Class classObj : classPackage.getClasses()) {
                // class name, member variables, and member functions
                sa.appendLine(String.format("class %s {", classObj.getName()));
                if (!skipMembers) {
                    sa.append(printMemberVariables(classObj.getMemberVariableRelations()));
                    sa.append(printMemberFunctions(classObj.getMemberFunctionRelations()));
                }
                sa.appendLine("}");
            }
            sa.appendLine("}");
        }
        // inheritance
        for (Component component : analysisContext.getComponents(ComponentCategory.PACKAGE)) {
            Package classPackage = Package.builder()
                                          .id(component.getId())
                                          .name(component.getName())
                                          .relations(component.getRelations())
                                          .build();
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
            Package classPackage = Package.builder()
                                          .id(component.getId())
                                          .name(component.getName())
                                          .relations(component.getRelations())
                                          .build();
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
            final Class classObj = Class.builder()
                                        .id(component.getId())
                                        .name(component.getName())
                                        .category(component.getComponentCategory())
                                        .relations(component.getRelations())
                                        .metadata(component.getMetadata())
                                        .build();
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
            if (!first.getComponentCategory().isClassCategory()) {
                continue;
            }
            final Component second = UMLRelation.getSecond();
            if (!second.getComponentCategory().isClassCategory()) {
                continue;
            }
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
            final Component relatedComp = funcRelation.getRelatedComponent();
            final Function func = Function.builder()
                                          .id(relatedComp.getId())
                                          .name(relatedComp.getName())
                                          .relations(relatedComp.getRelations())
                                          .build();
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

    @Override
    public String presentClusteringNodes(List<ClusteringNode> clusteringNodes) {
        Set<ClusteringNode> allNodes = new HashSet<>(clusteringNodes);
        Set<ClusteringNode> rendered = new HashSet<>();
        StringAppender sa = new StringAppender();
        sa.appendLine("@startuml");
        for (ClusteringNode node : clusteringNodes) {
            if (rendered.contains(node)) {
                continue;
            }
            List<String> nodeNames = getAllNodeNames(node);
            addAllNodesToRendered(node, rendered, allNodes);
            Iterator<String> itor = nodeNames.iterator();
            StringAppender compName = new StringAppender();
            while (itor.hasNext()) {
                String name = itor.next();
                if (itor.hasNext()) {
                    compName.append(name).append(", ");
                } else {
                    compName.append(name);
                }
            }
            sa.append("[").append(compName.toString()).appendLine("]");
        }
        sa.appendLine("@enduml");
        return sa.toString();
    }

    private List<String> getAllNodeNames(ClusteringNode node) {
        List<String> names = new LinkedList<>();
        for (Component childNode : node.getMemberNodes()) {
            if (childNode.getComponentCategory() == ComponentCategory.CLUSTERING_NODE) {
                names.addAll(getAllNodeNames((ClusteringNode) childNode));
            } else {
                names.add(childNode.getName());
            }
        }
        return names;
    }

    private void addAllNodesToRendered(ClusteringNode node, Set<ClusteringNode> rendered, Set<ClusteringNode> allNodes) {
        rendered.add(node);
        for (Component childNode : node.getMemberNodes()) {
            if (childNode.getComponentCategory() != ComponentCategory.CLUSTERING_NODE) {
                continue;
            }
            ClusteringNode childClusteringNode = (ClusteringNode) childNode;
            if (!allNodes.contains(childClusteringNode)) {
                continue;
            }
            addAllNodesToRendered(childClusteringNode, rendered, allNodes);
        }
    }

}
