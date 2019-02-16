package org.robinbird.main.newmodel2;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class TemplateClassType extends Type {

    private final List<Variable> variables;
    private final List<Function> functions;
    private final List<Type> templates;

    @Builder
    public TemplateClassType(final long id, @NonNull final String name,
                             @NonNull final List<Variable> variables,
                             @NonNull final List<Function> functions,
                             @NonNull final List<Type> templates) {
        super(id, name, TypeCategory.TEMPLATE_CLASS);
        this.variables = variables;
        this.functions = functions;
        this.templates = templates;
    }
}
