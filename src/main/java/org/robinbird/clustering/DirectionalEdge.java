package org.robinbird.clustering;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.robinbird.model.Component;

@Value
@AllArgsConstructor
public class DirectionalEdge implements Edge, Comparable<DirectionalEdge> {
    @NonNull
    private final Component from;
    @NonNull
    private final Component to;

    private final double weight;



    @Override
    public Component getFirstComponent() {
        return from;
    }

    @Override
    public Component getSecondComponent() {
        return to;
    }

    @Override
    public int compareTo(@NonNull final DirectionalEdge e) {
        return Edge.compare(this, e);
    }
}
