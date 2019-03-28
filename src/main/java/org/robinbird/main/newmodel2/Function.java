package org.robinbird.main.newmodel2;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Function extends AnalysisEntity {

    private final Type parent;
    private final List<Type> returnTypes; // there are a few languages returning multiple values from a function
    private final String name;
    private final List<Parameter> parameters;

}
