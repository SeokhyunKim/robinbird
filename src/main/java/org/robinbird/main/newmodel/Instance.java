package org.robinbird.main.newmodel;

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
