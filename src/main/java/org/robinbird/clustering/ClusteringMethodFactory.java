package org.robinbird.clustering;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.apache.commons.lang3.Validate;
import org.robinbird.util.Msgs;

@AllArgsConstructor
public class ClusteringMethodFactory {

    @NonNull
    private final ClusteringNodeFactory clusteringNodeFactory;

    public ClusteringMethod create(@NonNull final ClusteringMethodType type) {
        final ClusteringMethod clusteringMethod;
        switch (type) {
            default:
            case AGGLOMERATIVE:
                clusteringMethod = new AgglomerativeClustering(clusteringNodeFactory);
                break;
        }
        return clusteringMethod;
    }

    public double[] convertToParameters(@NonNull final String[] paramStrings) {
        Validate.isTrue(paramStrings.length >= 1, Msgs.get(Msgs.Key.INTERNAL_ERROR));
        final double[] params = new double[paramStrings.length];
        int i = 0;
        for (String paramStr : paramStrings) {
            params[i] = Double.parseDouble(paramStr);
        }
        return params;
    }
}
