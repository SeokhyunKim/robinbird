package org.robinbird.clustering;

public enum  ClusteringMethodType {
    AGGLOMERATIVE;

    public boolean isAgglomerativeClustering() {
        return this == AGGLOMERATIVE;
    }
}
