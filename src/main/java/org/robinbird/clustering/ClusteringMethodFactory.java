package org.robinbird.clustering;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.apache.commons.lang3.Validate;
import org.robinbird.util.Msgs;

@AllArgsConstructor
public class ClusteringMethodFactory {

    @NonNull
    private final ClusteringNodeFactory clusteringNodeFactory;

    public ClusteringMethod create(@NonNull final ClusteringMethodType type, @NonNull final Object... params) {
        final ClusteringMethod clusteringMethod;
        switch (type) {
            default:
            case AGGLOMERATIVE_CLUSTERING: {
                Validate.isTrue(params.length >= 2, Msgs.get(Msgs.Key.INTERNAL_ERROR));
                final double[] minMax = new double[2];
                int i = 0;
                for (Object param : params) {
                    minMax[i] = (double) param;
                    if (++i >= 2) {
                        break;
                    }
                }
                clusteringMethod = new AgglomerativeClustering(clusteringNodeFactory, minMax[0], minMax[1]);
            }
            break;
        }
        return clusteringMethod;
    }
}
