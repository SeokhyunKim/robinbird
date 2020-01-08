package org.robinbird.repository;

import static org.robinbird.util.Msgs.Key.TRIED_TO_CREATE_NEW_PERSISTED_RELATION_WITH_ALREADY_STORED;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.robinbird.exception.RobinbirdException;
import org.robinbird.model.Cardinality;
import org.robinbird.model.Component;
import org.robinbird.model.ComponentCategory;
import org.robinbird.model.Relation;
import org.robinbird.model.RelationCategory;
import org.robinbird.repository.dao.ComponentEntityDao;
import org.robinbird.repository.entity.ComponentEntity;
import org.robinbird.repository.entity.RelationEntity;
import org.robinbird.util.Msgs;

@Slf4j
public class ComponentRepository {

    private final ComponentEntityDao componentEntityDao;

    public ComponentRepository(@NonNull final ComponentEntityDao componentEntityDao) {
        this.componentEntityDao = componentEntityDao;
        Component.setComponentRepository(this);
    }

    /**
     * Get existing {@link Component} with the given name.
     *
     * @param name A name of AnalysisUnit.
     * @return Optional of found AnalysisUnit. If this doesn't find anything, returns empty optional.
     */
    public Optional<Component> getComponent(@NonNull final String name) {
        final Optional<ComponentEntity> ceOpt = componentEntityDao.loadComponentEntityByName(name);
        return ceOpt.map(Converter::convert);
    }

    public List<Component> getComponents(@NonNull final ComponentCategory componentCategory) {
        final List<ComponentEntity> entities = componentEntityDao.loadComponentEntities(componentCategory.name());
        return entities.stream().map(Converter::convert).collect(Collectors.toList());
    }

    public List<Relation> getRelations(@NonNull final Component parent) {
        final List<RelationEntity> entities = componentEntityDao.loadRelationEntities(parent.getId());
        final List<Relation> relations = new ArrayList<>(entities.size());
        entities.forEach(e -> {
            final Optional<ComponentEntity> ceOpt = componentEntityDao.loadComponentEntityById(e.getRelatedComponentId());
            ceOpt.ifPresent(aue -> {
                final RelationCategory relationCategory = RelationCategory.valueOf(e.getRelationCategory());
                relations.add(Relation.builder()
                                      .name(e.getName())
                                      .relationCategory(relationCategory)
                                      .relatedComponent(Converter.convert(ceOpt.get()))
                                      .cardinality(Cardinality.valueOf(e.getCardinality()))
                                      .parent(parent)
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
        final ComponentEntity saved = componentEntityDao.save(newEntity);
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
        component.getRelations().forEach(r -> compRelationsMap.computeIfAbsent(r.hashCode(), k -> new ArrayList<>()).add(r));

        // 1. delete relatedComponent entities not existing in component
        for (Map.Entry<Integer, List<Relation>> entry : dbRelationsMap.entrySet()) {
            final Integer relationHashCode = entry.getKey();
            final List<Relation> relationsInDb = entry.getValue();
            Iterator<Relation> itor = relationsInDb.iterator();
            int numDeleted;
            if (compRelationsMap.get(relationHashCode) == null) {
                numDeleted = relationsInDb.size();
            } else {
                numDeleted = relationsInDb.size() - compRelationsMap.get(relationHashCode).size();
            }
            while (itor.hasNext()) {
                if (numDeleted-- <= 0) {
                    break;
                }
                final Relation r = itor.next();
                componentEntityDao.delete(Converter.convert(r));
                itor.remove();
            }
        }

        // 2. add new relatedComponent entity not existing in db.
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
        componentEntityDao.update(entity);
    }

    private Relation persistNewRelation(@NonNull final Relation relation, @NonNull final Component parent) {
        Validate.isTrue(relation.getId() == null,
                        Msgs.get(TRIED_TO_CREATE_NEW_PERSISTED_RELATION_WITH_ALREADY_STORED, relation.toString()));
        final RelationEntity saved = componentEntityDao.save(Converter.convert(relation));
        final Optional<ComponentEntity> relatedComponentEntityOpt =
                componentEntityDao.loadComponentEntityById(saved.getRelatedComponentId());
        if (!relatedComponentEntityOpt.isPresent()) {
            throw new RobinbirdException(Msgs.get(Msgs.Key.INTERNAL_ERROR));
        }
        final Component relatedComponent = Converter.convert(relatedComponentEntityOpt.get());
        return Converter.convert(saved, relatedComponent, parent);
    }
}

