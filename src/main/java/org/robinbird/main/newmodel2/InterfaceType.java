package org.robinbird.main.newmodel2;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class InterfaceType extends Type {

    private final List<Function> functions;

    @Builder
    public InterfaceType(final long id, @NonNull final String name,
                         @NonNull final List<Function> functions) {
        super(id, name, TypeCategory.CLASS);
        this.functions = functions;
    }

}
