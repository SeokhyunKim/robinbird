package org.robinbird.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.robinbird.repository.ComponentRepository;

@Getter
@ToString
@EqualsAndHashCode(exclude = {"relations"})
public class Component {

    private static ComponentRepository componentRepository;

    public static void setComponentRepository(@NonNull final ComponentRepository componentRepository) {
        Component.componentRepository = componentRepository;
    }

    private final long id;
    @NonNull
    private final String name;
    @NonNull
    private ComponentCategory componentCategory;

    private List<Relation> relations;

    private Map<String, String> metadata;

    public Component(final long id, @NonNull final String name, @NonNull ComponentCategory componentCategory,
                     @Nullable final List<Relation> relations, @Nullable final Map<String, String> metadata) {
        this.id = id;
        this.name = name;
        this.componentCategory = componentCategory;
        this.relations = relations;
        this.metadata = metadata;
        if (this.metadata == null) {
            this.metadata = new HashMap<>();
        }
    }

    public void updateComponentCategory(@NonNull final ComponentCategory componentCategory) {
        this.componentCategory = componentCategory;
    }

    /**
     * Save current {@link Component} information in database. Current relations will overwrite database relations.
     */
    public void persist() {
        componentRepository.updateComponent(this);
    }

    /**
     * Get relations as an immutable list.
     * @return new list of relations.
     */
    public List<Relation> getRelations() {
        lazyLoadingRelations();
        return Lists.newArrayList(relations);
    }

    public List<Relation> getRelations(@NonNull final RelationCategory relationCategory) {
        return getRelations().stream().filter(r -> r.getRelationCategory() == relationCategory).collect(Collectors.toList());
    }

    /**
     * Add a new relatedComponent.
     * @param relation a new relatedComponent to add.
     */
    public void addRelation(@NonNull final Relation relation) {
        lazyLoadingRelations();
        this.relations.add(relation);
    }

    private void lazyLoadingRelations() {
        if (this.relations == null) {
            this.relations = componentRepository.getRelations(this);
        }
    }

    /**
     * Delete a relatedComponent.
     * @param relation a relatedComponent to be deleted.
     */
    public void deleteRelation(@NonNull final Relation relation) {
        lazyLoadingRelations();
        this.relations.removeIf(r -> (r.getName().equals(relation.getName())) &&
                                     (r.getParent().getId() == relation.getParent().getId()));

    }

    /**
     * Discard current relations and load relations from database.
     * Thus, all the unsaved relations will be lost. To save current relations, persist should be called beforehand.
     * This will load relations from database and replace current relations.
     * @return new relations list loaded from database.
     */
    public List<Relation> discardAndLoadRelations() {
        final List<Relation> loadedRelations = componentRepository.getRelations(this);
        this.relations.clear();
        this.relations.addAll(loadedRelations);
        return this.relations;
    }

    public Map<String, String> getMetadata() {
        return Maps.newHashMap(metadata);
    }

    public void setMetadata(@NonNull final Map<String, String> metadata) {
        this.metadata = new HashMap<>(metadata);
    }

    public void putMetadataValue(@NonNull final String key, @NonNull final String value) {
        metadata.put(key, value);
    }

    public String getMetadataValue(@NonNull final String key) {
        return metadata.get(key);
    }

}
