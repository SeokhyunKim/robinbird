package org.robinbird.main.oldrepository.dao.entity;

import java.util.Optional;
import lombok.NonNull;
import org.robinbird.main.oldmodel2.AccessModifier;
import org.robinbird.main.oldmodel2.Instance;
import org.robinbird.main.oldmodel2.Relation;
import org.robinbird.main.oldmodel2.RelationCategory;
import org.robinbird.main.oldmodel2.Type;
import org.robinbird.main.oldmodel2.TypeCategory;
import org.robinbird.main.oldrepository.dao.TypeDao;

public class EntityConverter {

    public static Type convert(@NonNull final TypeEntity entity) {
        return Type.builder()
                   .id(entity.getId())
                   .category(TypeCategory.valueOf(entity.getCategory()))
                   .name(entity.getName()).build();
    }

    public static TypeEntity convert(@NonNull final Type type) {
        TypeEntity entity = new TypeEntity();
        entity.setId(type.getId());
        entity.setName(type.getName());
        entity.setCategory(type.getCategory().name());
        return entity;
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
                       .accessModifier(
                    		   Optional.ofNullable(entity.getAccessModifier())
                    		   	.map(AccessModifier::valueOf)
                    		   	.orElse(null))
                       .build();
    }

    public static InstanceEntity convert(@NonNull final Instance instance, @NonNull final Type parentType) {
        InstanceEntity entity = new InstanceEntity();
        entity.setParentTypeId(parentType.getId());
        entity.setTypeId(instance.getType().getId());
        entity.setName(instance.getName());
        entity.setAccessModifier(
        		Optional.ofNullable(instance.getAccessModifier())
        			.map(AccessModifier::name)
        			.orElse(null));
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
