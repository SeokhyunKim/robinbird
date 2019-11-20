package org.robinbird.repository.dao;

import java.util.List;
import java.util.Optional;
import org.robinbird.repository.entity.ComponentEntity;
import org.robinbird.repository.entity.RelationEntity;

public interface ComponentEntityDao {

    Optional<ComponentEntity> loadComponentEntity(long id);

    Optional<ComponentEntity> loadComponentEntity(String name);

    List<ComponentEntity> loadComponentEntities(String componentCategory);

    Optional<RelationEntity> loadRelationEntity(long parentId, String id);

    List<RelationEntity> loadRelationEntities(long parentId);

    int getNumComponentEntities();

    <T> T save(T entity);

    <T> T update(T entity);

    <T> void delete(T entry);

    void deleteAll(); // mainly for unit tests

    void close();

}
