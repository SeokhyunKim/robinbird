package org.robinbird.repository;

import static org.robinbird.util.Msgs.Key.TRIED_TO_CREATE_NEW_PERSISTED_RELATION_WITH_ALREADY_STORED;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.robinbird.exception.RobinbirdException;
import org.robinbird.model.Cardinality;
import org.robinbird.model.Component;
import org.robinbird.model.ComponentCategory;
import org.robinbird.model.CurrentRbRepository;
import org.robinbird.model.Relation;
import org.robinbird.model.RelationCategory;
import org.robinbird.repository.dao.EntityDao;
import org.robinbird.repository.entity.ComponentEntity;
import org.robinbird.repository.entity.RelationEntity;
import org.robinbird.util.Msgs;

@Slf4j
public class RbRepository {

    private final EntityDao entityDao;

    public RbRepository(@NonNull final EntityDao entityDao) {
        this.entityDao = entityDao;
        CurrentRbRepository.setRbRepository(this);
        log.info("Update the current RbRepository: {}", this);
    }

    /**
     * Get existing {@link Component} with the given name.
     *
     * @param name A name of AnalysisUnit.
     * @return Optional of found AnalysisUnit. If this doesn't find anything, returns empty optional.
     */
    public Optional<Component> getComponent(@NonNull final String name) {
        final List<ComponentEntity> entities = entityDao.loadComponentEntityByName(name);
        // when component has name and owner, it means a composite component like container or array.
        // except composite components, thee should be one component per each distinct name.
        final List<ComponentEntity> independentComponents = entities.stream()
                                                                    .filter(e -> StringUtils.isEmpty(e.getOwnerId()))
                                                                    .collect(Collectors.toList());
        if (independentComponents.isEmpty()) {
            return Optional.empty();
        }
        if (independentComponents.size() > 1) {
            throw new RobinbirdException(Msgs.get(Msgs.Key.MULTIPLE_INDEPENDENT_COMPONENTS_OF_SAME_NAME));
        }
        return Optional.of(Converter.convert(independentComponents.get(0)));
    }

    public Optional<Component> getDependentComponent(@NonNull final String name, @NonNull final Component owner) {
        return entityDao.loadComponentEntityByNameAndOwnerId(name, owner.getId()).map(Converter::convert);
    }

    public List<Component> getComponents(@NonNull final ComponentCategory componentCategory) {
        final List<ComponentEntity> entities = entityDao.loadComponentEntities(componentCategory.name());
        return entities.stream().map(Converter::convert).collect(Collectors.toList());
    }

    public List<Relation> getRelations(@NonNull final Component owner) {
        final List<RelationEntity> entities = entityDao.loadRelationEntities(owner.getId());
        final List<Relation> relations = new ArrayList<>(entities.size());
        entities.forEach(e -> {
            final Optional<ComponentEntity> ceOpt = entityDao.loadComponentEntityById(e.getRelatedComponentId());
            ceOpt.ifPresent(aue -> {
                final RelationCategory relationCategory = RelationCategory.valueOf(e.getRelationCategory());
                relations.add(Relation.builder()
                                      .name(e.getName())
                                      .relationCategory(relationCategory)
                                      .relatedComponent(Converter.convert(ceOpt.get()))
                                      .cardinality(Cardinality.valueOf(e.getCardinality()))
                                      .owner(owner)
                                      .id(e.getId())
                                      .build());
            });
        });
        return relations;
    }

    /**
     * Register new {@link Component} with the given name and category.
     * If this is trying to register already existing {@link Component}, it will be just returned.
     * If this is trying to register a name of existing one with different category,
     * {@link org.robinbird.exception.RobinbirdException} will be thrown.
     *
     * @param name
     * @param category
     * @return
     */
    public Component registerComponent(@NonNull final String name, @NonNull final ComponentCategory category) {
        final Optional<Component> componentOpt = getComponent(name);
        if (componentOpt.isPresent()) {
            final Component component = componentOpt.get();
            Validate.isTrue(component.getComponentCategory() == category,
                            "Trying to register Component with different " +
                                    "category. Name: " + name + ", category: " + category.name());
            return component;
        }
        final ComponentEntity newEntity = new ComponentEntity();
        newEntity.setName(name);
        newEntity.setComponentCategory(category.name());
        final ComponentEntity saved = entityDao.save(newEntity);
        return Converter.convert(saved);
    }

    public Component registerDependentComponent(@NonNull final String name, @NonNull final ComponentCategory category,
                                                @NonNull final Component owner) {
        final Optional<Component> dependentCompOpt = getDependentComponent(name, owner);
        if (dependentCompOpt.isPresent()) {
            final Component dependentComp = dependentCompOpt.get();
            Validate.isTrue(dependentComp.getComponentCategory() == category,
                            "Trying to register Component with different " +
                                    "category. Name: " + name + ", category: " + category.name());
            return dependentComp;
        }
        final ComponentEntity newEntity = new ComponentEntity();
        newEntity.setName(name);
        newEntity.setComponentCategory(category.name());
        newEntity.setOwnerId(owner.getId());
        final ComponentEntity saved = entityDao.save(newEntity);
        return Converter.convert(saved);
    }

    /**
     * After creating {@link Component}, {@link Relation} can be added to it.
     * To persist those added new relations, this function should be called.
     * This will add new relations and delete relations which are not hold by the given component.
     * In other words, this will make the state of persistent storage same with the component in memory.
     *
     * @param component a component want to make its relations synced with persistent storage.
     */
    public void updateComponent(@NonNull final Component component) {
        updateComponentWithoutChangingRelations(component);

        // Relation loaded from db
        final List<Relation> dbRelations = getRelations(component);
        final Map<Integer, List<Relation>> dbRelationsMap = new HashMap<>();
        dbRelations.forEach(r -> dbRelationsMap.computeIfAbsent(r.hashCode(), k -> new ArrayList<>()).add(r));

        // Relations wanted to be updated.
        final Map<Integer, List<Relation>> compRelationsMap = new HashMap<>();
        // component.getRelations() can return current not persisted relations.
        component.getRelationsList().forEach(r -> compRelationsMap.computeIfAbsent(r.hashCode(), k -> new ArrayList<>())
                                                                  .add(r));

        // 1. delete relation entities not existing in component
        for (Map.Entry<Integer, List<Relation>> entry : dbRelationsMap.entrySet()) {
            final Integer relationHashCode = entry.getKey();
            final List<Relation> relationsInDb = entry.getValue();
            final List<Relation> compRelations = compRelationsMap.get(relationHashCode);
            Iterator<Relation> itor = relationsInDb.iterator();
            while (itor.hasNext()) {
                final Relation dbRelation = itor.next();
                // hash code conflict should be rare. so this would be ok in terms of performance
                final boolean isExisting = compRelations.stream().anyMatch(r -> r.equals(dbRelation));
                if (!isExisting) {
                    entityDao.delete(Converter.convert(dbRelation));
                    itor.remove();
                }
            }
        }

        // 2. add new component entity not existing in db.
        for (final List<Relation> relationsInComp : compRelationsMap.values()) {
            relationsInComp.forEach(r -> {
                if (!r.isPersisted()) {
                    component.deleteRelationObject(r); // this is deleting based on memory reference, not equals
                    component.addRelation(persistNewRelation(r, component));
                }
            });
        }
    }

    /**
     * Updating {@link Component} without changing relations.
     *
     * @param component AnalysisUnit needs to be updated.
     */
    public void updateComponentWithoutChangingRelations(@NonNull final Component component) {
        final ComponentEntity entity = Converter.convert(component);
        entityDao.update(entity);
    }

    private Relation persistNewRelation(@NonNull final Relation relation, @NonNull final Component parent) {
        Validate.isTrue(relation.getId() == null,
                        Msgs.get(TRIED_TO_CREATE_NEW_PERSISTED_RELATION_WITH_ALREADY_STORED, relation.toString()));
        final RelationEntity saved = entityDao.save(Converter.convert(relation));
        final Optional<ComponentEntity> relatedComponentEntityOpt =
                entityDao.loadComponentEntityById(saved.getRelatedComponentId());
        if (!relatedComponentEntityOpt.isPresent()) {
            throw new RobinbirdException(Msgs.get(Msgs.Key.INTERNAL_ERROR));
        }
        final Component relatedComponent = Converter.convert(relatedComponentEntityOpt.get());
        return Converter.convert(saved, relatedComponent, parent);
    }
}

