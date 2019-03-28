package org.robinbird.main.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
@EqualsAndHashCode(exclude = {"relations"})
public class AnalysisEntity {

    private final String name;
    private final AnalysisEntityCategory category;

    protected final Map<RelationType, List<Relation>> relations = new HashMap<>();

    public List<Relation> getRelations(@NonNull final RelationType relationType) {
        return relations.get(relationType);
    }

    public void addRelation(@NonNull final RelationType relationType, @NonNull final Relation relation) {
        relations.computeIfAbsent(relationType, rt -> new ArrayList<>())
                 .add(relation);
    }

    // todo: need to add type checking logic here just to return simple name for classes?
    public String getSimpleName() {
        int lastIdx = this.name.lastIndexOf(ModelConstants.SEPERATOR);
        if (lastIdx == -1 || (lastIdx + 1) >= (this.name.length() - 1)) {
            return this.name;
        }
        return this.name.substring(lastIdx + 1);
    }


}
