package org.robinbird.main.newmodel2;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Parameter {

    private final Type type;

    private final DerivedTypeCategory derivedTypeCategory;

}
