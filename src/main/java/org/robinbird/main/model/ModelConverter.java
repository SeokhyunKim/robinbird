package org.robinbird.main.model;

import lombok.NonNull;
import org.robinbird.main.dao.entity.TypeEntity;

public class ModelConverter {

    public static TypeEntity convert(@NonNull final Type type) {
        TypeEntity entity = new TypeEntity();
        entity.setName(type.getName());
        entity.setType(TypeEntity.class.getSimpleName());
        entity.setKind(type.getKind().name());
        entity.setVarargs(type.isVarargs());
        return entity;
    }
}
