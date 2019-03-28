package org.robinbird.main.repository.dao;

import org.robinbird.main.repository.entity.Relation;
import org.robinbird.main.repository.entity.RelationKey;
import org.springframework.data.repository.CrudRepository;

public interface RelationRepository extends CrudRepository<Relation, RelationKey> {
}
