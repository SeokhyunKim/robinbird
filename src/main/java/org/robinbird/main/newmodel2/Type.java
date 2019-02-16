package org.robinbird.main.newmodel2;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class Type {

    private final long id;
    private final String name;
    private final TypeCategory typeCategory;

    @Builder
    public Type(final long id, @NonNull final String name, @NonNull final TypeCategory typeCategory) {
        this.id = id;
        this.name = name;
        this.typeCategory = typeCategory;
    }

    public String getSimpleName() {
        int lastIdx = this.name.lastIndexOf(ModelKey.SEPERATOR);
        if (lastIdx == -1 || (lastIdx + 1) >= (this.name.length() - 1)) {
            return this.name;
        }
        return this.name.substring(lastIdx + 1);
    }

}
