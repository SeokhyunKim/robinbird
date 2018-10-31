package org.robinbird.main.newmodel;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
@EqualsAndHashCode
public class Relation {

    private final RelationCategory category;
    private final Type type;

}
