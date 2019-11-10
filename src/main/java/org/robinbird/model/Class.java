package org.robinbird.model;

import static org.robinbird.model.Cardinality.MULTIPLE;
import static org.robinbird.model.Cardinality.ONE;
import static org.robinbird.model.ComponentCategory.ARRAY;
import static org.robinbird.model.ComponentCategory.COLLECTION;
import static org.robinbird.model.ComponentCategory.PACKAGE;
import static org.robinbird.util.JsonObjectMapper.OBJECT_MAPPER;
import static org.robinbird.util.Msgs.Key.INTERNAL_ERROR;
import static org.robinbird.util.Msgs.Key.JSON_PROCESSING_ISSUE;
import static org.robinbird.util.Msgs.Key.WRONG_COMPONENT_CATEGORY;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import java.io.IOException;
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
import org.robinbird.exception.RobinbirdException;
import org.robinbird.util.Msgs;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class Class extends Component {

    private static final String TEMPLATE_VARIABLES_KEY = "templateVariables";

    @Builder
    private Class(final long id, @NonNull final String name, @NonNull final ComponentCategory category,
                  @Nullable final List<Relation> relations, @Nullable final Map<String, String> metadata) {
        super(id, name, category, relations, metadata);
        Validate.isTrue(category == ComponentCategory.CLASS ||
                        category == ComponentCategory.TEMPLATE_CLASS ||
                        category == ComponentCategory.INTERFACE,
                        Msgs.get(Msgs.Key.INVALID_COMPONENT_CATEGORY, category.name()));
    }

    public void setTemplateVariables(@NonNull final List<String> vars) {
        try {
            final String templateVarsString = OBJECT_MAPPER.writeValueAsString(vars);
            putMetadataValue(TEMPLATE_VARIABLES_KEY, templateVarsString);
        } catch (final JsonProcessingException e) {
            throw new RobinbirdException(Msgs.get(JSON_PROCESSING_ISSUE, vars.toString()), e);
        }
    }

    public List<String> getTemplateVariables() {
        if (getComponentCategory() != ComponentCategory.TEMPLATE_CLASS) {
            return Lists.newArrayList();
        }
        final String templateVarsString = getMetadataValue(TEMPLATE_VARIABLES_KEY);
        if (templateVarsString == null) {
            return Lists.newArrayList();
        }
        try {
            return OBJECT_MAPPER.readValue(templateVarsString, new TypeReference<List<String>>() {
            });
        } catch (final IOException e) {
            throw new RobinbirdException(Msgs.get(JSON_PROCESSING_ISSUE, templateVarsString), e);
        }
    }

    public void setParent(@NonNull final Class parent) {
        final ComponentCategory category = parent.getComponentCategory();
        Validate.isTrue(category == ComponentCategory.CLASS ||
                                category == ComponentCategory.TEMPLATE_CLASS ||
                                category == ComponentCategory.INTERFACE,
                        Msgs.get(Msgs.Key.INVALID_COMPONENT_CATEGORY, category.name()));
        final Relation parentRelation = Relation.builder()
                                                .relationCategory(RelationCategory.PARENT_CLASS)
                                                .relatedComponent(parent)
                                                .parent(this)
                                                .build();
        this.addRelation(parentRelation);
    }

    public Optional<Class> getParent() {
        final List<Relation> relations = this.getRelations(RelationCategory.PARENT_CLASS);
        Validate.isTrue(relations.size() <= 1, Msgs.get(INTERNAL_ERROR));
        if (relations.isEmpty()) {
            return Optional.empty();
        }
        final Component relatedComp = relations.iterator().next().getRelatedComponent();
        return Optional.of(Class.builder()
                                .id(relatedComp.getId())
                                .name(relatedComp.getName())
                                .category(relatedComp.getComponentCategory())
                                .relations(relatedComp.getRelations())
                                .metadata(relatedComp.getMetadata())
                                .build());
    }

    public void addInterface(@NonNull final Class newInterface) {
        // todo: check whether this is safe
        //Validate.isTrue(newInterface.getComponentCategory() == ComponentCategory.INTERFACE,
        //                Msgs.get(Msgs.Key.INVALID_COMPONENT_CATEGORY, newInterface.getComponentCategory().name()));
        final Relation interfaceRelation = Relation.builder()
                                                .relationCategory(RelationCategory.IMPLEMENTING_INTERFACE)
                                                .relatedComponent(newInterface)
                                                .parent(this)
                                                .build();
        this.addRelation(interfaceRelation);
    }

    public List<Class> getInterfaces() {
        final List<Relation> relations = this.getRelations(RelationCategory.IMPLEMENTING_INTERFACE);
        if (relations.isEmpty()) {
            return Lists.newArrayList();
        }
        return relations.stream().map(r -> (Class) r.getRelatedComponent()).collect(Collectors.toList());
    }

    public void addMemberVariable(@NonNull final Component memberVariableType, @NonNull final String name,
                                  @NonNull final AccessLevel accessLevel) {
        final Cardinality cardinality = getMemberVariableCardinality(memberVariableType.getComponentCategory());
        final Relation memberVariable = Relation.builder()
                                                .name(name)
                                                .relationCategory(RelationCategory.MEMBER_VARIABLE)
                                                .relatedComponent(memberVariableType)
                                                .cardinality(cardinality)
                                                .parent(this)
                                                .build();
        memberVariable.setAccessLevel(accessLevel);
        this.addRelation(memberVariable);
    }

    public List<Relation> getMemberVariableRelations() {
        return getRelations(RelationCategory.MEMBER_VARIABLE);
    }

    public void addMemberFunction(@NonNull final Function memberFunction, @NonNull final String methodName,
                                  @NonNull final AccessLevel accessLevel) {
        final Relation method = Relation.builder()
                                        .name(methodName)
                                        .relationCategory(RelationCategory.MEMBER_FUNCTION)
                                        .relatedComponent(memberFunction)
                                        .parent(this)
                                        .build();
        method.setAccessLevel(accessLevel);
        this.addRelation(method);
    }

    public List<Relation> getMemberFunctionRelations() {
        return getRelations(RelationCategory.MEMBER_FUNCTION);
    }

    private Cardinality getMemberVariableCardinality(@NonNull final ComponentCategory category) {
        Validate.isTrue(category != PACKAGE, Msgs.get(WRONG_COMPONENT_CATEGORY, category.name()));
        if (category == COLLECTION || category == ARRAY) {
            return MULTIPLE;
        }
        return ONE;
    }
}
