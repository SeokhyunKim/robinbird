package org.robinbird.repository.dao;

import org.robinbird.repository.entity.AnalysisEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalysisEntityRepository extends CrudRepository<AnalysisEntity, Long> {

}
