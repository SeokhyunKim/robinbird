package org.robinbird.main.newmodel;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Builder
@Getter
@ToString
@EqualsAndHashCode
public class Relation {

    private final RelationCategory category;
    private final Type type;
    
    public static Relation create(@NonNull final RelationCategory category, @NonNull final Type type) {
    	return Relation.builder().category(category).type(type).build();
    }

}
