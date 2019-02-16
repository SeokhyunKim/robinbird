package org.robinbird.main.newmodel2;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Variable {

    private final Type parent;
    private final Type type;
    private final DerivedTypeCategory derivedTypeCategory;
    private final String name;

}
