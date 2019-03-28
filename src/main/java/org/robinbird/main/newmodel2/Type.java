package org.robinbird.main.newmodel2;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@Getter
@EqualsAndHashCode
public class Type extends AnalysisEntity {

    private final long id;
    private final String name;
    private final AnalysisEntityCategory analysisEntityCategory;

    protected Type(final long id, @NonNull final String name, @NonNull final AnalysisEntityCategory analysisEntityCategory) {
        this.id = id;
        this.name = name;
        this.analysisEntityCategory = analysisEntityCategory;
    }

    public String getSimpleName() {
        int lastIdx = this.name.lastIndexOf(ModelConstants.SEPERATOR);
        if (lastIdx == -1 || (lastIdx + 1) >= (this.name.length() - 1)) {
            return this.name;
        }
        return this.name.substring(lastIdx + 1);
    }

}
