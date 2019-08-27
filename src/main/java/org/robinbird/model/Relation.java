package org.robinbird.model;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@Getter
@EqualsAndHashCode(exclude = {"id"})
@Builder
public class Relation {

    private static final String COLLECTION_TYPE_KEY = "collectionType";

    @Builder.Default
    private final String name = "";
    @NonNull
    private final RelationCategory relationCategory;
    @NonNull
    private final Component relatedComponent;
    @Builder.Default
    private final Cardinality cardinality = Cardinality.ONE;

    @NonNull
    private final Component parent;
    private final String id;

    @Builder.Default
    private final Map<String, String> metadata = new HashMap<>();

    @Override
    public String toString() {
        return "Relation(name=" + name +
                   ", relationCategory=" + relationCategory.name() +
                   ", relatedComponent=" + relatedComponent.getName() +
                   ", cardinality=" + cardinality +
                   ", parent=" + parent.getName() +
                   ", metadata=" + metadata.toString() + ")";
    }

    public void setAccessLevel(@NonNull final AccessLevel accessLevel) {
        metadata.put(AccessLevel.class.getSimpleName(), accessLevel.name());
    }

    public AccessLevel getAccessLevel() {
        final String accessLevelStr = metadata.get(AccessLevel.class.getSimpleName());
        if (accessLevelStr == null) {
            return AccessLevel.PRIVATE;
        }
        return AccessLevel.valueOf(accessLevelStr);
    }

    public void setCollectionType(@NonNull final String collectionType) {
        metadata.put(COLLECTION_TYPE_KEY, collectionType);
    }

    public String getCollectionType() {
        return metadata.get(COLLECTION_TYPE_KEY);
    }

    public boolean isCollection() {
        return metadata.get(COLLECTION_TYPE_KEY) != null;
    }



}
