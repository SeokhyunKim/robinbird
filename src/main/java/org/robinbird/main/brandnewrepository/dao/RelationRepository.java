package org.robinbird.main.brandnewrepository.dao;

import org.robinbird.main.brandnewrepository.entity.Relation;
import org.robinbird.main.brandnewrepository.entity.RelationKey;
import org.springframework.data.repository.CrudRepository;

public interface RelationRepository extends CrudRepository<Relation, RelationKey> {
}
