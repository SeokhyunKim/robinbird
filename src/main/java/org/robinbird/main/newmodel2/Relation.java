package org.robinbird.main.newmodel2;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Relation {

    private final RelationType relationType;
    private final AnalysisEntity analysisEntity;
    @Builder.Default
    private final int cardinality = 1;

}
