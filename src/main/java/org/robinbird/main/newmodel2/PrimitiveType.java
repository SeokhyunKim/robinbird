package org.robinbird.main.newmodel2;

import lombok.Builder;
import lombok.NonNull;

public class PrimitiveType extends Type {

    @Builder
    public PrimitiveType(final long id, @NonNull final String name) {
        super(id, name, TypeCategory.PRIMITIVE);
    }

}
