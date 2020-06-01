package org.robinbird.repository.dao;

import java.util.List;
import java.util.Optional;
import org.robinbird.repository.entity.ComponentEntity;
import org.robinbird.repository.entity.RelationEntity;

public interface EntityDao {

    Optional<ComponentEntity> loadComponentEntityById(String id);

    Optional<ComponentEntity> loadComponentEntityByNameAndOwnerId(String name, String ownerId);

    List<ComponentEntity> loadComponentEntityByName(String name);

    List<ComponentEntity> loadComponentEntities(String componentCategory);

    Optional<RelationEntity> loadRelationEntity(String parentId, String id);

    List<RelationEntity> loadRelationEntities(String parentId);

    int getNumComponentEntities();

    <T> T save(T entity);

    <T> T update(T entity);

    <T> void delete(T entry);

    void deleteAll(); // mainly for unit tests

    void close();

}
