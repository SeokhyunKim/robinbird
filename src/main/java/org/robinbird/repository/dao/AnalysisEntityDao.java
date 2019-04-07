package org.robinbird.repository.dao;

import java.util.List;
import java.util.Optional;
import org.robinbird.repository.entity.AnalysisEntity;
import org.robinbird.repository.entity.Relation;

public interface AnalysisEntityDao {

    Optional<AnalysisEntity> loadAnalysisEntity(long id);

    Optional<AnalysisEntity> loadAnalysisEntity(String name);

    Optional<Relation> loadRelation(long id);

    List<Relation> loadRelations(long analysisEntityId);

    <T> T save(T entity);

    <T> T update(T entity);

    <T> void delete(T entry);

}
