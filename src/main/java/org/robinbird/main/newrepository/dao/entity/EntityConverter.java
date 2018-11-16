package org.robinbird.main.newrepository.dao.entity;

import java.util.Optional;
import lombok.NonNull;
import org.robinbird.main.newmodel.AccessModifier;
import org.robinbird.main.newmodel.Instance;
import org.robinbird.main.newmodel.Relation;
import org.robinbird.main.newmodel.RelationCategory;
import org.robinbird.main.newmodel.Type;
import org.robinbird.main.newmodel.TypeCategory;
import org.robinbird.main.newrepository.dao.TypeDao;

public class EntityConverter {

    public static Type convert(@NonNull final TypeEntity entity) {
        return Type.builder()
                   .id(entity.getId())
                   .category(TypeCategory.valueOf(entity.getCategory()))
                   .name(entity.getName()).build();
    }

    public static Type convert(@NonNull final CompositionTypeEntity cte, @NonNull final TypeDao dao) {
        return dao.loadTypeEntity(cte.getCompositionTypeId()).map(EntityConverter::convert).get();
    }

    public static CompositionTypeEntity convert(@NonNull final Type compositionType, @NonNull final Type type) {
        CompositionTypeEntity entity = new CompositionTypeEntity();
        entity.setTypeId(type.getId());
        entity.setCompositionTypeId(compositionType.getId());
        return entity;
    }

    public static Instance convert(@NonNull final InstanceEntity entity, @NonNull final TypeDao dao) {
        Optional<TypeEntity> teOpt = dao.loadTypeEntity(entity.getTypeId());
        if (!teOpt.isPresent()) {
            return null;
        }
        Type type = convert(teOpt.get());
        return Instance.builder()
                       .type(type)
                       .name(entity.getName())
                       .accessModifier(AccessModifier.valueOf(entity.getAccessModifier()))
                       .build();
    }

    public static InstanceEntity convert(@NonNull final Instance instance, @NonNull final Type parentType) {
        InstanceEntity entity = new InstanceEntity();
        entity.setParentTypeId(parentType.getId());
        entity.setTypeId(instance.getType().getId());
        entity.setName(instance.getName());
        entity.setAccessModifier(instance.getAccessModifier().name());
        return entity;
    }

    public static Relation convert(@NonNull final RelationEntity entity, @NonNull final TypeDao dao) {
        Type type = dao.loadTypeEntity(entity.getTypeId()).map(EntityConverter::convert).get();
        if (type == null) {
            return null;
        }
        return Relation.builder()
                       .type(type)
                       .category(RelationCategory.valueOf(entity.getCategory()))
                       .build();
    }

    public static RelationEntity convert(@NonNull final Relation relation, @NonNull final Type parentType) {
        RelationEntity entity = new RelationEntity();
        entity.setParentTypeId(parentType.getId());
        entity.setTypeId(relation.getType().getId());
        entity.setCategory(relation.getCategory().name());
        return entity;
    }
}
