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

    List<TypeEntity> loadChildTypeEntities(long parentId);

    List<TypeEntity> loadAllTypeEntities();

    TypeEntity saveTypeEntity(TypeEntity typeEntity);

    TypeEntity updateTypeEntity(TypeEntity typeEntity);

    void removeTypeEntity(TypeEntity typeEntity);

    void removeTypeEntity(long id);

    List<CompositionTypeEntity> loadCompositionTypeEntities(long typeId);

    CompositionTypeEntity saveCompositionTypeEntity(CompositionTypeEntity compositionTypeEntity);

    void removeCompositionTypeEntities(long typeId);

    List<InstanceEntity> loadInstanceEntities(long parentTypeId);

    InstanceEntity saveInstanceEntity(InstanceEntity instanceEntity);

    InstanceEntity updateInstanceEntity(InstanceEntity instanceEntity);

    void removeInstanceEntity(InstanceEntity instanceEntity);

    void removeInstanceEntities(long parentTypeId);

    List<RelationEntity> loadRelationEntities(long parentTypeId);

    RelationEntity saveRelationEntity(RelationEntity relationEntity);

    RelationEntity updateRelationEntity(RelationEntity relationEntity);

    void removeRelationEntity(RelationEntity relationEntity);

    void removeRelationEntities(long parentTypeId);


}
