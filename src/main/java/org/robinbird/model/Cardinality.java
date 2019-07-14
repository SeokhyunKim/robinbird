package org.robinbird.model;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.NonNull;

public enum Cardinality {
    ONE("1"), MULTIPLE("n");

    private String string;
    private static Map<String, Cardinality> cardinalityMap = Arrays.stream(Cardinality.values())
                                                                   .collect(Collectors.toMap(Cardinality::toString,
                                                                                             Function.identity()));

    Cardinality(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return this.string;
    }

    public static Cardinality fromString(@NonNull final String string) {
        return cardinalityMap.get(string);
    }

}
