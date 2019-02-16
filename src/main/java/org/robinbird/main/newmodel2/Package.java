package org.robinbird.main.newmodel2;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class Package extends Type {

    private final List<Type> types;
    private final List<Package> packages;

    @Builder
    public Package(final long id, @NonNull final String name,
                   @NonNull List<Type> types,
                   @NonNull List<Package> packages) {
        super(id, name, TypeCategory.PACKAGE);
        this.types = types;
        this.packages = packages;
    }
}
