package org.robinbird.clustering;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import org.robinbird.model.Component;

@Value
@AllArgsConstructor
public class Edge implements Comparable<Edge> {
    @NonNull
    private final Component from;
    @NonNull
    private final Component to;

    private final double weight;

    public int compareTo(@NonNull final Edge e) {
        if (this.getWeight() == e.getWeight()) {
            return 0;
        }
        if (this.getWeight() < e.getWeight()) {
            return -1;
        }
        return 1;
    }
}
