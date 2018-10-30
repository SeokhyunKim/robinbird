package org.robinbird.main.newrepository.dao;

import java.util.List;
import java.util.Optional;
import org.robinbird.main.newrepository.dao.entity.CompositionTypeEntity;
import org.robinbird.main.newrepository.dao.entity.InstanceEntity;
import org.robinbird.main.newrepository.dao.entity.RelationEntity;
import org.robinbird.main.newrepository.dao.entity.TypeEntity;

public interface TypeDao {

    Optional<TypeEntity> loadTypeEntity(long id);

    Optional<TypeEntity> loadTypeEntity(String name);

    TypeEntity saveTypeEntity(TypeEntity typeEntity);

    void removeTypeEntity(TypeEntity typeEntity);

    List<CompositionTypeEntity> loadCompositionTypeEntities(long typeId);

    CompositionTypeEntity saveCompositionTypeEntity(CompositionTypeEntity compositionTypeEntity);

    void removeCompositionTypeEntities(long typeId);

    List<InstanceEntity> loadInstanceEntities(long parentTypeId);

    InstanceEntity saveInstanceEntity(InstanceEntity instanceEntity);

    void removeInstanceEntity(InstanceEntity instanceEntity);

    void removeInstanceEntities(long parentTypeId);

    List<RelationEntity> loadRelationEntities(long parentTypeId);

    RelationEntity saveRelationEntity(RelationEntity relationEntity);

    void removeRelationEntity(RelationEntity relationEntity);

    void removeRelationEntities(long parentTypeId);


}
