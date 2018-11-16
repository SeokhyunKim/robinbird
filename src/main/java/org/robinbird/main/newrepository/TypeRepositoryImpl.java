package org.robinbird.main.newrepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.robinbird.main.newmodel.Instance;
import org.robinbird.main.newmodel.Relation;
import org.robinbird.main.newmodel.Type;
import org.robinbird.main.newmodel.TypeCategory;
import org.robinbird.main.newrepository.dao.TypeDao;
import org.robinbird.main.newrepository.dao.entity.CompositionTypeEntity;
import org.robinbird.main.newrepository.dao.entity.EntityConverter;
import org.robinbird.main.newrepository.dao.entity.InstanceEntity;
import org.robinbird.main.newrepository.dao.entity.RelationEntity;
import org.robinbird.main.newrepository.dao.entity.TypeEntity;

@AllArgsConstructor
public class TypeRepositoryImpl implements TypeRepository {

    @NonNull
    private final TypeDao dao;

    public Type registerType(TypeCategory category, String name) {
        TypeEntity te = new TypeEntity();
        te.setCategory(category.name());
        te.setName(name);
        dao.saveTypeEntity(te);
        return EntityConverter.convert(te);
    }

    public Optional<Type> getType(long id) {
        return dao.loadTypeEntity(id).map(EntityConverter::convert);
    }

    public Optional<Type> getType(@NonNull final String name) {
        return dao.loadTypeEntity(name).map(EntityConverter::convert);
    }

    public void deleteType(final long id) {
        dao.removeTypeEntity(id);
    }

    public void deleteType(@NonNull final String name) {
        dao.loadTypeEntity(name).ifPresent(dao::removeTypeEntity);
    }

    public Type populateType(@NonNull final Type type) {
        final List<CompositionTypeEntity> ctes = dao.loadCompositionTypeEntities(type.getId());
        final List<InstanceEntity> ies = dao.loadInstanceEntities(type.getId());
        final List<RelationEntity> res = dao.loadRelationEntities(type.getId());
        final List<Type> compositionTypes = ctes.stream()
                                                .map(cte -> EntityConverter.convert(cte, dao))
                                                .collect(Collectors.toList());
        final List<Instance> instances = ies.stream()
                                            .map(ie -> EntityConverter.convert(ie, dao))
                                            .collect(Collectors.toList());
        final List<Relation> relations = res.stream()
                                            .map(re -> EntityConverter.convert(re, dao))
                                            .collect(Collectors.toList());
        return type.populate(compositionTypes, instances, relations);
    }

    public void updateType(@NonNull final Type type) {
        final List<Type> compositionTypes = type.getCompositionTypes();
        compositionTypes.forEach(ct -> {
            CompositionTypeEntity cte = EntityConverter.convert(ct, type);
            dao.saveCompositionTypeEntity(cte);
        });
        final List<Instance> instances = type.getInstances();
        instances.forEach(i -> {
            InstanceEntity ie = EntityConverter.convert(i, type);
            InstanceEntity updated = dao.updateInstanceEntity(ie);
            if (updated == null) {
                dao.saveInstanceEntity(ie);
            }
        });
        final List<Relation> relations = type.getRelations();
        relations.forEach(r -> {
            RelationEntity re = EntityConverter.convert(r, type);
            RelationEntity updated = dao.updateRelationEntity(re);
            if (updated == null) {
                dao.saveRelationEntity(re);
            }
        });
    }
}
