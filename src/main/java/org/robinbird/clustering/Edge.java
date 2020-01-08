package org.robinbird.clustering;

import lombok.NonNull;
import org.apache.commons.lang3.tuple.Pair;
import org.robinbird.model.Component;

public interface Edge {

    Component getFirstComponent();
    Component getSecondComponent();

    double getWeight();

    static int compare(@NonNull final Edge e1, @NonNull final Edge e2) {
        if (Double.compare(e1.getWeight(), e2.getWeight()) == 0) {
            final String e1First = e1.getFirstComponent().getName();
            final String e1Second = e1.getSecondComponent().getName();
            final String e2First = e2.getFirstComponent().getName();
            final String e2Second = e2.getSecondComponent().getName();
            if (e1First.equals(e2First) && e1Second.equals(e2Second)) {
                return 0;
            }
            if (e1First.compareTo(e2First) < 0) {
                return -1;
            } else if (e1First.compareTo(e2First) > 0) {
                return 1;
            } if (e1Second.compareTo(e2Second) < 0) {
                return -1;
            }
            return 1;
        }
        if (Double.compare(e1.getWeight(), e2.getWeight()) < 0) {
            return 1;
        }
        return -1;
    }

}
