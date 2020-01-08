package org.robinbird.clustering;

import lombok.NonNull;
import lombok.Value;
import org.robinbird.model.Component;

@Value
public class BidirectionalEdge implements Edge, Comparable<BidirectionalEdge> {
    @NonNull
    private final Component component1;
    @NonNull
    private final Component component2;

    private final double weight;

    public BidirectionalEdge(@NonNull final Component component1, @NonNull final Component component2, final double weight) {
        if (component1.getName().compareTo(component2.getName()) < 0) {
            this.component1 = component1;
            this.component2 = component2;
        } else {
            this.component1 = component2;
            this.component2 = component1;
        }
        this.weight = weight;
    }

    @Override
    public Component getFirstComponent() {
        return component1;
    }

    @Override
    public Component getSecondComponent() {
        return component2;
    }

    @Override
    public int compareTo(@NonNull final BidirectionalEdge e) {
        return Edge.compare(this, e);
    }
}
