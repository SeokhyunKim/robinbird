package org.robinbird.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.robinbird.exception.AlreadyExistingAnalysisEntityException;
import org.robinbird.model.Cardinality;
import org.robinbird.model.Component;
import org.robinbird.model.ComponentCategory;
import org.robinbird.model.Relation;
import org.robinbird.model.RelationCategory;
import org.robinbird.repository.dao.ComponentEntityDao;
import org.robinbird.repository.entity.ComponentEntity;
import org.robinbird.repository.entity.RelationEntity;

@Slf4j
public class ComponentRepository {

    private final ComponentEntityDao componentEntityDao;

    public ComponentRepository(@NonNull final ComponentEntityDao componentEntityDao) {
        this.componentEntityDao = componentEntityDao;
        Component.setComponentRepository(this);
    }

    /**
     * Get existing {@link Component} with the given name.
     * @param name A name of AnalysisUnit.
     * @return Optional of found AnalysisUnit. If this doesn't find anything, returns empty optional.
     */
    public Optional<Component> getComponent(@NonNull final String name) {
        final Optional<ComponentEntity> aueOpt = componentEntityDao.loadComponentEntity(name);
        return aueOpt.map(Converter::convert);
    }

    public List<Component> getComponents(@NonNull final ComponentCategory componentCategory) {
        final List<ComponentEntity> entities = componentEntityDao.loadComponentEntities(componentCategory.name());
        return entities.stream().map(Converter::convert).collect(Collectors.toList());
    }

    public List<Relation> getRelations(@NonNull final Component parent) {
        final List<RelationEntity> entities = componentEntityDao.loadRelationEntities(parent.getId());
        final List<Relation> relations = new ArrayList<>(entities.size());
        entities.forEach(e -> {
            final Optional<ComponentEntity> ceOpt = componentEntityDao.loadComponentEntity(e.getRelationId());
            ceOpt.ifPresent(aue -> {
                final RelationCategory relationCategory = RelationCategory.valueOf(e.getRelationCategory());
                relations.add(Relation.builder()
                                      .name(e.getName())
                                      .relationCategory(relationCategory)
                                      .relatedComponent(Converter.convert(ceOpt.get()))
                                      .cardinality(Cardinality.fromString(e.getCardinality()))
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
     * {@link AlreadyExistingAnalysisEntityException} will be thrown.
     * @param name
     * @param category
     * @return
     */
    public Component registerComponent(@NonNull final String name, @NonNull final ComponentCategory category) {
        final Optional<Component> componentOpt = getComponent(name);
        if (componentOpt.isPresent()) {
            final Component component = componentOpt.get();
            Validate.isTrue(component.getComponentCategory() == category,
                            "Trying to register AnalysisUnit with different " +
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
     * This will add new relations and delete relations which are not hold by the given analysisUnit.
     * In other words, this will make the state of persistent storage same with the analysisUnit in memory.
     * @param component an AnalysisUnit want to make its relations synced with persistent storage.
     */
    public void updateComponent(@NonNull final Component component) {
        updateComponentWithoutChangingRelations(component);

        // Relation loaded from db
        final Map<Relation, Relation> dbRelations = getRelations(component)
                                                            .stream()
                                                            .collect(Collectors.toMap(Function.identity(), Function.identity()));

        // Relations wanted to be updated.
        final Map<Relation, Relation> compRelations = component.getRelations() // this can return current not persisted relations.
                                                                .stream()
                                                                .collect(Collectors.toMap(Function.identity(), Function.identity()));

        // 1. delete relatedComponent entities not existing in component
        dbRelations.values().forEach(r -> {
            if (compRelations.get(r) == null) {
                componentEntityDao.delete(Converter.convert(r));
            }
        });
        // 2. add new relatedComponent entity not existing in db.
        compRelations.values().forEach(r -> {
            if (dbRelations.get(r) == null) {
                componentEntityDao.save(Converter.convert(r));
            }
        });
    }

    /**
     * Updating {@link Component} without changing relations.
     * @param component AnalysisUnit needs to be updated.
     */
    public void updateComponentWithoutChangingRelations(@NonNull final Component component) {
        final ComponentEntity entity = Converter.convert(component);
        componentEntityDao.update(entity);
    }
}
