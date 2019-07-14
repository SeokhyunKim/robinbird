package org.robinbird.clustering;

import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import lombok.Builder;
import lombok.NonNull;

public class AgglomerativeClusteringNode extends ClusteringNode {

    private static final String SCORE_KEY = "score";

    @Builder
    public AgglomerativeClusteringNode(final long id, @Nullable final String name, @NonNull final Map<String, String> metadata) {
        super(id, name, null, metadata);
    }

    public void setScore(final double score) {
        putMetadataValue(SCORE_KEY, Double.toString(score));
    }

    public double getScore() {
        return Optional.ofNullable(getMetadataValue(SCORE_KEY)).map(Double::parseDouble).orElse(0.0);
    }

}
