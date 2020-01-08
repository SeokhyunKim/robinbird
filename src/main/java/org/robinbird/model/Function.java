package org.robinbird.model;

import static org.robinbird.util.Msgs.Key.FOUND_COMPONENT_OF_DIFFERENT_TYPE;
import static org.robinbird.util.Msgs.Key.INTERNAL_ERROR;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import lombok.Builder;
import lombok.NonNull;
import org.apache.commons.lang3.Validate;
import org.robinbird.util.Msgs;

public class Function extends Component {

    private static String PARAMETER_ORDER = "parameterOrder";

    @Builder
    public Function(@NonNull final String id, @NonNull final String name, @Nullable final List<Relation> relations) {
        super(id, name, ComponentCategory.FUNCTION, relations, null);
    }

    public void addParameters(@Nullable final List<Component> parameters) {
        if (parameters == null) {
            return;
        }
        int order = 0;
        for (final Component param : parameters) {
            final Map<String, String> metadata = Maps.newHashMap();
            metadata.put(PARAMETER_ORDER, Integer.toString(++order));
            final Relation relation = Relation.builder()
                                              .parent(this)
                                              .relatedComponent(param)
                                              .relationCategory(RelationCategory.FUNCTION_PARAMETER)
                                              .metadata(metadata)
                                              .build();
            addRelation(relation);
        }
    }

    public List<Component> getParameters() {
        final List<Relation> params = getRelations(RelationCategory.FUNCTION_PARAMETER);
        params.sort((p1, p2) -> {
            final String odrStr1 = Optional.ofNullable(p1.getMetadata().get(PARAMETER_ORDER)).orElse("0");
            final String odrStr2 = Optional.ofNullable(p2.getMetadata().get(PARAMETER_ORDER)).orElse("0");
            return Integer.parseInt(odrStr1) - Integer.parseInt(odrStr2);
        });
        return params.stream().map(Relation::getRelatedComponent).collect(Collectors.toList());
    }

    public void addReturnType(@NonNull final Component returnType) {
        final Relation relation = Relation.builder()
                                          .parent(this)
                                          .relatedComponent(returnType)
                                          .relationCategory(RelationCategory.FUNCTION_RETURN_TYPE)
                                          .build();
        addRelation(relation);
    }

    public Component getReturnType() {
        final List<Relation> relations = getRelations(RelationCategory.FUNCTION_RETURN_TYPE);
        Validate.isTrue(relations.size() == 1, Msgs.get(INTERNAL_ERROR));
        return relations.iterator().next().getRelatedComponent();
    }

    public static Function create(@NonNull final Component component) {
        Validate.isTrue(component.getComponentCategory() == ComponentCategory.FUNCTION,
                        Msgs.get(FOUND_COMPONENT_OF_DIFFERENT_TYPE, component.getName(), component.getComponentCategory().name()));
        return Function.builder()
                       .id(component.getId())
                       .name(component.getName())
                       .build();
    }

}
