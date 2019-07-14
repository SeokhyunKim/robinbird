package org.robinbird.main.oldmodel2;

import javax.annotation.Nullable;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
@EqualsAndHashCode
public class Instance {

    private final Type type;
    private final String name;
    @Nullable
    private final AccessModifier accessModifier;

}
