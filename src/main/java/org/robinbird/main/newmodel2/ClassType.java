package org.robinbird.main.newmodel2;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class ClassType extends Type {

    private final List<Variable> variables;
    private final List<Function> functions;

    @Builder
    public ClassType(final long id, @NonNull final String name,
                     @NonNull final List<Variable> variables,
                     @NonNull final List<Function> functions) {
        super(id, name, TypeCategory.CLASS);
        this.variables = variables;
        this.functions = functions;
    }
}
