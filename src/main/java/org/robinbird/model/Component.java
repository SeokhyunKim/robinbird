package org.robinbird.model;

import static org.robinbird.util.Msgs.Key.INTERNAL_ERROR;
import static org.robinbird.util.Utils.deepCopyMap;

import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.Validate;
import org.robinbird.util.Msgs;

@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Component {

    @EqualsAndHashCode.Include
    @NonNull
    private final String id;

    @NonNull
    private final String name;

    @NonNull
    private ComponentCategory componentCategory;

    private Map<RelationCategory, List<Relation>> relations;

    @NonNull
    private Map<String, String> metadata;

    public Component(@NonNull final String id, @NonNull final String name, @NonNull ComponentCategory componentCategory,
                     @Nullable final Map<RelationCategory, List<Relation>> relations, @Nullable final Map<String, String> metadata) {
        this.id = id;
        this.name = name;
        this.componentCategory = componentCategory;
        if (relations != null) {
            this.relations = deepCopyMap(relations);
        }
        this.metadata = metadata;
        if (this.metadata == null) {
            this.metadata = new HashMap<>();
        }
    }

    public Component(@NonNull final String id, @NonNull final String name, @NonNull ComponentCategory componentCategory,
                     @Nullable final Collection<Relation> relations, @Nullable final Map<String, String> metadata) {
        this.id = id;
        this.name = name;
        this.componentCategory = componentCategory;
        if (relations != null) {
            this.relations = deepCopyMap(relations.stream()
                                                  .collect(Collectors.groupingBy(Relation::getRelationCategory)));
        }
        this.metadata = metadata;
        if (this.metadata == null) {
            this.metadata = new HashMap<>();
        }
    }

    public void updateComponentCategory(@NonNull final ComponentCategory componentCategory) {
        this.componentCategory = componentCategory;
    }

    public Map<RelationCategory, List<Relation>> getRelations() {
        lazyLoadingRelations();
        return deepCopyMap(this.relations);
    }

    public List<Relation> getRelationsList() {
        lazyLoadingRelations();
        return this.relations.values().stream().flatMap(List::stream).collect(Collectors.toList());
    }

    public List<Relation> getRelationsList(@NonNull final RelationCategory relationCategory) {
        lazyLoadingRelations();
        final List<Relation> relations = this.relations.get(relationCategory);
        if (relations == null) {
            return new ArrayList<>();
        }
        return relations;
    }

    /**
     * Add a new relatedRbType.
     * @param relation a new relatedRbType to add.
     */
    public void addRelation(@NonNull final Relation relation) {
        lazyLoadingRelations();
        this.relations.computeIfAbsent(relation.getRelationCategory(), k -> new ArrayList<>())
                      .add(relation);
    }

    private void lazyLoadingRelations() {
        if (this.relations == null) {
            final List<Relation> relations = CurrentRbRepository.getRelations(this);
            this.relations = new HashMap<>();
            relations.forEach(r -> this.relations.computeIfAbsent(r.getRelationCategory(), k -> new ArrayList<>())
                                                 .add(r));
        }
    }

    /**
     * Delete a relatedRbType.
     * @param relation a relatedRbType to be deleted.
     */
    // todo: revist the comparing logic of this function
    public void deleteRelation(@NonNull final Relation relation) {
        lazyLoadingRelations();
        if (!this.relations.containsKey(relation.getRelationCategory())) {
            return;
        }
        this.relations.get(relation.getRelationCategory())
                      .removeIf(r -> r.equals(relation) &&
                                     (r.getOwner().getId() == relation.getOwner().getId()));
    }

    public void deleteRelationObject(@NonNull final Relation relation) {
        lazyLoadingRelations();
        if (!this.relations.containsKey(relation.getRelationCategory())) {
            return;
        }
        this.relations.get(relation.getRelationCategory())
                      .removeIf(r -> r == relation);
    }

    public void deleteRelationByCategory(@NonNull final RelationCategory relationCategory) {
        if (!this.relations.containsKey(relationCategory)) {
            return;
        }
        this.relations.get(relationCategory)
                      .removeIf(r -> r.getRelationCategory() == relationCategory);
    }

    /**
     * Discard current relations and load relations from database.
     * Thus, all the unsaved relations will be lost. To save current relations, persist should be called beforehand.
     * This will load relations from database and replace current relations.
     * @return new relations map loaded from database.
     */
    public Map<RelationCategory, List<Relation>> discardAndLoadRelations() {
        final List<Relation> loadedRelations = CurrentRbRepository.getRelations(this);
        this.relations.clear();
        loadedRelations.forEach(r -> this.relations.computeIfAbsent(r.getRelationCategory(), k -> new ArrayList<>())
                                                   .add(r));
        return getRelations();
    }

    public void setOwnerComponent(@NonNull final Component owner) {
        lazyLoadingRelations();
        deleteRelationByCategory(RelationCategory.OWNER_COMPONENT);
        final Relation parentRelation = Relation.builder()
                                                .relationCategory(RelationCategory.OWNER_COMPONENT)
                                                .relatedComponent(owner)
                                                .owner(this)
                                                .build();
        this.addRelation(parentRelation);
    }

    public Optional<Component> getOwnerComponent() {
        lazyLoadingRelations();
        if (CollectionUtils.isEmpty(this.relations.get(RelationCategory.OWNER_COMPONENT))) {
            return Optional.empty();
        }
        final List<Relation> relations = this.relations.get(RelationCategory.OWNER_COMPONENT);
        Validate.isTrue(relations.size() <= 1, Msgs.get(INTERNAL_ERROR));
        final Component relatedComp = relations.iterator().next().getRelatedComponent();
        return Optional.of(Class.builder()
                                .id(relatedComp.getId())
                                .name(relatedComp.getName())
                                .category(relatedComp.getComponentCategory())
                                .relations(relatedComp.getRelations())
                                .metadata(relatedComp.getMetadata())
                                .build());
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

    static public Component createComponentWithoutPersistence(@NonNull ComponentCategory componentCategory) {
        return new Component(UUID.randomUUID().toString(), UUID.randomUUID().toString(), componentCategory,
                             (List<Relation>)null, null);
    }
}
