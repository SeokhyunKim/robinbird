package org.robinbird.model;

import static org.robinbird.model.Cardinality.MULTIPLE;
import static org.robinbird.model.Cardinality.ONE;
import static org.robinbird.model.ComponentCasts.toClass;
import static org.robinbird.model.ComponentCategory.ARRAY;
import static org.robinbird.model.ComponentCategory.CONTAINER;
import static org.robinbird.model.ComponentCategory.PACKAGE;
import static org.robinbird.util.Msgs.Key.INTERNAL_ERROR;
import static org.robinbird.util.Msgs.Key.WRONG_COMPONENT_CATEGORY;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.apache.commons.lang3.Validate;
import org.robinbird.util.JsonObjectMapper;
import org.robinbird.util.Msgs;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class Class extends Component {

    private static final String TEMPLATE_VARIABLES_KEY = "templateVariables";

    @Builder
    private Class(@NonNull final String id, @NonNull final String name,
                  @NonNull final ComponentCategory category,
                  @Nullable final Map<RelationCategory, List<Relation>> relations,
                  @Nullable final Map<String, String> metadata) {
        super(id, name, category, relations, metadata);
        Validate.isTrue(category.isClassCategory(),
                        Msgs.get(Msgs.Key.INVALID_COMPONENT_CATEGORY, category.name()));
    }

    public void setTemplateVariables(@NonNull final List<String> vars) {
        final String templateVarsString = JsonObjectMapper.writeValueAsString(vars);
        putMetadataValue(TEMPLATE_VARIABLES_KEY, templateVarsString);
    }

    public List<String> getTemplateVariables() {
        if (getComponentCategory() != ComponentCategory.TEMPLATE_CLASS) {
            return Lists.newArrayList();
        }
        final String templateVarsString = getMetadataValue(TEMPLATE_VARIABLES_KEY);
        if (templateVarsString == null) {
            return Lists.newArrayList();
        }
        return JsonObjectMapper.readValue(templateVarsString, new TypeReference<List<String>>(){});
    }

    public void setParentClass(@NonNull final Class parent) {
        final ComponentCategory parentCategory = parent.getComponentCategory();
        Validate.isTrue(parentCategory.isClassCategory(),
                        Msgs.get(Msgs.Key.INVALID_COMPONENT_CATEGORY, parentCategory.name()));
        deleteRelationByCategory(RelationCategory.PARENT_CLASS);
        final Relation parentRelation = Relation.builder()
                                                .relationCategory(RelationCategory.PARENT_CLASS)
                                                .relatedComponent(parent)
                                                .owner(this)
                                                .build();
        this.addRelation(parentRelation);
    }

    public Optional<Class> getParentClass() {
        final List<Relation> relations = this.getRelationsList(RelationCategory.PARENT_CLASS);
        if (relations.isEmpty()) {
            return Optional.empty();
        }
        Validate.isTrue(relations.size() <= 1, Msgs.get(INTERNAL_ERROR));

        final Component relatedComp = relations.iterator().next().getRelatedComponent();
        return Optional.of(Class.builder()
                                .id(relatedComp.getId())
                                .name(relatedComp.getName())
                                .category(relatedComp.getComponentCategory())
                                .relations(relatedComp.getRelations())
                                .metadata(relatedComp.getMetadata())
                                .build());
    }

    public void setPackage(@NonNull final Package parentPackage) {
        final ComponentCategory category = parentPackage.getComponentCategory();
        Validate.isTrue(category == PACKAGE,
                        Msgs.get(Msgs.Key.INVALID_COMPONENT_CATEGORY, category.name()));
        final Relation packageRelation = Relation.builder()
                                                .relationCategory(RelationCategory.PARENT_PACKAGE)
                                                .relatedComponent(parentPackage)
                                                .owner(this)
                                                .build();
        this.addRelation(packageRelation);
    }

    public Optional<Package> getPackage() {
        final List<Relation> relations = this.getRelationsList(RelationCategory.PARENT_PACKAGE);
        if (relations.isEmpty()) {
            return Optional.empty();
        }
        final Component relatedComp = relations.iterator().next().getRelatedComponent();
        return Optional.of(Package.create(relatedComp));
    }

    public void addInterface(@NonNull final Class newInterface) {
        Validate.isTrue(newInterface.getComponentCategory() == ComponentCategory.INTERFACE,
                        Msgs.get(Msgs.Key.INVALID_COMPONENT_CATEGORY, newInterface.getComponentCategory().name()));
        final Relation interfaceRelation = Relation.builder()
                                                .relationCategory(RelationCategory.IMPLEMENTING_INTERFACE)
                                                .relatedComponent(newInterface)
                                                .owner(this)
                                                .build();
        this.addRelation(interfaceRelation);
    }

    public List<Class> getInterfaces() {
        final List<Relation> relations = this.getRelationsList(RelationCategory.IMPLEMENTING_INTERFACE);
        if (relations.isEmpty()) {
            return Lists.newArrayList();
        }
        return relations.stream().map(r -> toClass(r.getRelatedComponent())).collect(Collectors.toList());
    }

    public void addMemberVariable(@NonNull final Component memberVariableType, @NonNull final String name,
                                  @NonNull final AccessLevel accessLevel) {
        final Cardinality cardinality = getMemberVariableCardinality(memberVariableType.getComponentCategory());
        final Relation memberVariable = Relation.builder()
                                                .name(name)
                                                .relationCategory(RelationCategory.MEMBER_VARIABLE)
                                                .relatedComponent(memberVariableType)
                                                .cardinality(cardinality)
                                                .owner(this)
                                                .build();
        memberVariable.setAccessLevel(accessLevel);
        this.addRelation(memberVariable);
    }

    public List<Relation> getMemberVariableRelations() {
        return getRelationsList(RelationCategory.MEMBER_VARIABLE);
    }

    public void addMemberFunction(@NonNull final Function memberFunction, @NonNull final String methodName,
                                  @NonNull final AccessLevel accessLevel) {
        final Relation method = Relation.builder()
                                        .name(methodName)
                                        .relationCategory(RelationCategory.MEMBER_FUNCTION)
                                        .relatedComponent(memberFunction)
                                        .owner(this)
                                        .build();
        method.setAccessLevel(accessLevel);
        this.addRelation(method);
    }

    public List<Relation> getMemberFunctionRelations() {
        return getRelationsList(RelationCategory.MEMBER_FUNCTION);
    }

    private Cardinality getMemberVariableCardinality(@NonNull final ComponentCategory category) {
        Validate.isTrue(category != PACKAGE, Msgs.get(WRONG_COMPONENT_CATEGORY, category.name()));
        if (category == CONTAINER || category == ARRAY) {
            return MULTIPLE;
        }
        return ONE;
    }
}
