package org.robinbird.main.model;

import lombok.NonNull;
import org.robinbird.main.repository.dao.entity.ClassEntity;
import org.robinbird.main.repository.dao.entity.TypeEntity;

public class ModelConverter {

    public static TypeEntity convert(@NonNull final Type type) {
        TypeEntity entity = new TypeEntity();
        entity.setName(type.getName());
        entity.setType(TypeEntity.class.getSimpleName());
        entity.setKind(type.getKind().name());
        return entity;
    }

    public static ClassEntity convert(@NonNull final Class classObj) {
        ClassEntity entity = new ClassEntity();
        return null;

    }
}
