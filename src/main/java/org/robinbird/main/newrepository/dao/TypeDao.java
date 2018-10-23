package org.robinbird.main.newrepository.dao;

import java.util.List;
import java.util.Optional;
import org.robinbird.main.newrepository.dao.entity.InstanceEntity;
import org.robinbird.main.newrepository.dao.entity.RelationEntity;
import org.robinbird.main.newrepository.dao.entity.TypeEntity;

public interface TypeDao {

    Optional<TypeEntity> loadTypeEntity(long id);

    Optional<TypeEntity> loadTypeEntity(String name);

    List<TypeEntity> loadTypeEntities(List<Long> ids);

    List<Long> loadCompositionTypeIds(long id);

//    List<InstanceEntity> loadInstanceEntities(long parentId);
//
//    List<RelationEntity> loadRelationEntities(long parentId);

    TypeEntity saveTypeEntity(TypeEntity te);

    void saveCompositionTypeIds(long parentId, List<Long> ids);

//    InstanceEntity saveInstanceEntity(InstanceEntity ie);
//
//    RelationEntity saveReleationEntity(RelationEntity re);

    void deleteTypeEntity(TypeEntity te);

    void deleteCompositionTypeIds(long parentId, List<Long> ids);

//    void deleteInstanceEntity(InstanceEntity ie);
//
//    void deleteReleationEntity(RelationEntity re);

}
