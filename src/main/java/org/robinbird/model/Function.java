package org.robinbird.model;

import static org.robinbird.util.Msgs.Key.INTERNAL_ERROR;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Validate;
import org.robinbird.util.Msgs;

public class Function extends Component {

    private static String PARAMETER_ORDER = "parameterOrder";

    private Function(@NonNull final String id, @NonNull final String name,
                     @Nullable final Map<String, String> metadata) {
        super(id, name, ComponentCategory.FUNCTION, (List<Relation>)null, metadata);
    }

    private void setReturnType(@NonNull final Component returnType) {
        deleteRelationByCategory(RelationCategory.FUNCTION_RETURN_TYPE);
        final Relation relation = Relation.builder()
                                          .owner(this)
                                          .relatedComponent(returnType)
                                          .relationCategory(RelationCategory.FUNCTION_RETURN_TYPE)
                                          .build();
        addRelation(relation);
    }

    public Optional<Component> getReturnType() {
        final List<Relation> relations = getRelationsList(RelationCategory.FUNCTION_RETURN_TYPE);
        Validate.isTrue(relations.size() <= 1, Msgs.get(INTERNAL_ERROR));
        if (relations.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(relations.get(0).getRelatedComponent());
    }

    private void setParameters(@Nullable final List<Component> parameters) {
        if (CollectionUtils.isEmpty(parameters)) {
            return;
        }
        deleteRelationByCategory(RelationCategory.FUNCTION_PARAMETER);
        int order = 0;
        for (final Component param : parameters) {
            final Map<String, String> metadata = Maps.newHashMap();
            metadata.put(PARAMETER_ORDER, Integer.toString(order++));
            final Relation relation = Relation.builder()
                                              .owner(this)
                                              .relatedComponent(param)
                                              .relationCategory(RelationCategory.FUNCTION_PARAMETER)
                                              .metadata(metadata)
                                              .build();
            addRelation(relation);
        }
    }

    public List<Component> getParameters() {
        final List<Relation> params = getRelationsList(RelationCategory.FUNCTION_PARAMETER);
        params.sort((p1, p2) -> {
            final String odrStr1 = Optional.ofNullable(p1.getMetadata().get(PARAMETER_ORDER)).orElse("0");
            final String odrStr2 = Optional.ofNullable(p2.getMetadata().get(PARAMETER_ORDER)).orElse("0");
            return Integer.parseInt(odrStr1) - Integer.parseInt(odrStr2);
        });
        return params.stream().map(Relation::getRelatedComponent).collect(Collectors.toList());
    }

    public static Function create(@NonNull final String id,
                                  @NonNull final Component returnType,
                                  @NonNull final String functionName,
                                  @NonNull final List<Component> parameters,
                                  @NonNull final Component owner) {
        final Function function = new Function(id, createFunctionName(functionName, parameters), null);
        function.setOwnerComponent(owner);
        function.setReturnType(returnType);
        function.setParameters(parameters);
        return function;
    }

    public static Function create(@NonNull final Component component) {
        final Function function = new Function(component.getId(), component.getName(), component.getMetadata());
        Validate.isTrue(component.getOwnerComponent().isPresent(), Msgs.get(Msgs.Key.INTERNAL_ERROR));
        return function;
    }

    public static String createFunctionName(@NonNull final String functionName,
                                            @NonNull final List<Component> parameters) {
        final StringBuilder sb = new StringBuilder();
        sb.append(functionName);
        sb.append("(");
        final Iterator<Component> itor = parameters.iterator();
        if (itor.hasNext()) {
            sb.append(itor.next().getName());
        }
        while (itor.hasNext()) {
            sb.append(", ").append(itor.next().getName());
        }
        sb.append(")");
        return sb.toString();
    }
}
