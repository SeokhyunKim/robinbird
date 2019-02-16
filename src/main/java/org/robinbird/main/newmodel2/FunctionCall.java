package org.robinbird.main.newmodel2;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class FunctionCall extends Statement {

    private final Type type;
    private final Function function;

    @Builder
    public FunctionCall(@NonNull final Type type, @NonNull final Function function) {
        super(StatementCategory.FUNCTION_CALL);
        this.type = type;
        this.function = function;
    }

}