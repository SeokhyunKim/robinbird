package org.robinbird.main.dao.entity;

import lombok.NonNull;
import org.robinbird.main.model.RobinbirdObject;
import org.robinbird.main.model.Type;

public class EntityConverter {

    public static RobinbirdObject convert(@NonNull final RobinbirdEntity entity) {
        return new RobinbirdObject(entity.getId(), entity.getName());
    }

    public static RobinbirdObject convert(@NonNull final TypeEntity typeEntity) {
        return Type.builder()
                .id(typeEntity.getId())
                .name(typeEntity.getName())
                .kind(Type.Kind.valueOf(typeEntity.getKind()))
                .varargs(typeEntity.isVarargs())
                .build();
    }
}
