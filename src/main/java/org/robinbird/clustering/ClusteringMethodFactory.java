package org.robinbird.clustering;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.apache.commons.lang3.Validate;
import org.robinbird.util.Msgs;

@AllArgsConstructor
public class ClusteringMethodFactory {

    @NonNull
    private final ClusteringNodeFactory clusteringNodeFactory;

    public ClusteringMethod create(@NonNull final ClusteringMethodType type, @NonNull final String[] paramStrings) {
        final ClusteringMethod clusteringMethod;
        switch (type) {
            default:
            case AGGLOMERATIVE_CLUSTERING: {
                Validate.isTrue(paramStrings.length >= 2, Msgs.get(Msgs.Key.INTERNAL_ERROR));
                final double[] minMax = new double[2];
                int i = 0;
                for (String paramStr : paramStrings) {
                    minMax[i] = Double.parseDouble(paramStr);
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

    public ClusteringNodeMatcher getNodeMatcher(@NonNull final ClusteringMethodType type) {
        final ClusteringNodeMatcher nodeMatcher;
        switch (type) {
            default:
            case AGGLOMERATIVE_CLUSTERING:
                nodeMatcher = AgglomerativeClusteringNodeMatchers::matchScoreRange;
        }
        return nodeMatcher;
    }
}
