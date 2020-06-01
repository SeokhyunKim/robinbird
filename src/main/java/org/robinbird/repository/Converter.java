package org.robinbird.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.robinbird.model.Cardinality;
import org.robinbird.model.Component;
import org.robinbird.model.ComponentCategory;
import org.robinbird.model.Relation;
import org.robinbird.model.RelationCategory;
import org.robinbird.repository.entity.ComponentEntity;
import org.robinbird.repository.entity.RelationEntity;
import org.robinbird.util.JsonObjectMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Converter {

    public static Component convert(@NonNull final ComponentEntity entity) {
        final Map<String, String> metadata;
        if (entity.getMetadata() != null) {
            metadata = JsonObjectMapper.readValue(entity.getMetadata(), new TypeReference<Map<String, String>>(){});
        } else {
            metadata = Maps.newHashMap();
        }

        return new Component(entity.getId(), entity.getName(), ComponentCategory.valueOf(entity.getComponentCategory()),
                             (List<Relation>)null, metadata);
    }

    public static ComponentEntity convert(@NonNull final Component component) {
        final ComponentEntity entity = new ComponentEntity();
        entity.setId(component.getId());
        entity.setName(component.getName());
        entity.setComponentCategory(component.getComponentCategory().name());
        entity.setMetadata(JsonObjectMapper.writeValueAsString(component.getMetadata()));
        final Optional<Component> ownerOpt = component.getOwnerComponent();
        ownerOpt.ifPresent(owner -> entity.setOwnerId(owner.getId()));
        return entity;
    }

    public static RelationEntity convert(@NonNull final Relation relation) {
        final RelationEntity entity = new RelationEntity();
        entity.setOwnerId(relation.getOwner().getId());
        entity.setId(relation.getId());
        entity.setRelationCategory(relation.getRelationCategory().name());
        entity.setName(relation.getName());
        entity.setRelatedComponentId(relation.getRelatedComponent().getId());
        entity.setCardinality(relation.getCardinality().name());
        entity.setMetadata(JsonObjectMapper.writeValueAsString(relation.getMetadata()));
        return entity;
    }

    public static Relation convert(@NonNull final RelationEntity entity,
                                   @NonNull final Component relatedComponent, @NonNull final Component owner) {
        final Map<String, String> metadata =
            JsonObjectMapper.readValue(entity.getMetadata(), new TypeReference<Map<String, String>>(){});
        return Relation.builder()
                       .owner(owner)
                       .id(entity.getId())
                       .name(Optional.ofNullable(entity.getName()).orElse(""))
                       .relationCategory(RelationCategory.valueOf(entity.getRelationCategory()))
                       .relatedComponent(relatedComponent)
                       .cardinality(Cardinality.valueOf(entity.getCardinality()))
                       .metadata(metadata)
                       .build();
    }
}
