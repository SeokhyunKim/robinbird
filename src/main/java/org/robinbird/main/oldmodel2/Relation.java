package org.robinbird.main.oldmodel2;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Builder
@Getter
@ToString
@EqualsAndHashCode(exclude = {"cardinality"})
public class Relation {

    private final RelationCategory category;
    private final Type type;
    private final int cardinality;

    public Relation withCardinality(final int cardinality) {
        return Relation.builder().category(this.category).type(this.type).cardinality(cardinality).build();
    }
    
    public static Relation create(@NonNull final RelationCategory category, @NonNull final Type type) {
    	return Relation.builder().category(category).type(type).cardinality(1).build();
    }

    public static Relation create(@NonNull final RelationCategory category, @NonNull final Type type, final int cardinality) {
        return Relation.builder().category(category).type(type).cardinality(cardinality).build();
    }

}
