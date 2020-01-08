package org.robinbird.repository;

import static org.robinbird.util.JsonObjectMapper.OBJECT_MAPPER;
import static org.robinbird.util.Msgs.Key.JSON_PROCESSING_ISSUE;
import static org.robinbird.util.Msgs.Key.NULL_POINTER_ENCOUNTERED;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.robinbird.exception.RobinbirdException;
import org.robinbird.model.Cardinality;
import org.robinbird.model.Component;
import org.robinbird.model.ComponentCategory;
import org.robinbird.model.Relation;
import org.robinbird.model.RelationCategory;
import org.robinbird.repository.entity.ComponentEntity;
import org.robinbird.repository.entity.RelationEntity;
import org.robinbird.util.Msgs;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Converter {

    private static ObjectMapper objectMapper = OBJECT_MAPPER;

    public static Component convert(@NonNull final ComponentEntity entity) {
        try {
            final Map<String, String> metadata;
            if (entity.getMetadata() != null) {
                metadata = objectMapper.readValue(entity.getMetadata(), new TypeReference<Map<String, String>>(){});
            } else {
                metadata = Maps.newHashMap();
            }

            return new Component(entity.getId(), entity.getName(), ComponentCategory.valueOf(entity.getComponentCategory()),
                                 null, metadata);
        } catch (final IOException e) {
            throw new RobinbirdException(Msgs.get(JSON_PROCESSING_ISSUE, entity.toString()), e);
        } catch (final NullPointerException e) {
            throw new RobinbirdException(Msgs.get(NULL_POINTER_ENCOUNTERED, entity.toString()), e);
        }
    }

    public static ComponentEntity convert(@NonNull final Component component) {
        final ComponentEntity entity = new ComponentEntity();
        entity.setId(component.getId());
        entity.setName(component.getName());
        entity.setComponentCategory(component.getComponentCategory().name());

        try {
            entity.setMetadata(objectMapper.writeValueAsString(component.getMetadata()));
        } catch (final JsonProcessingException e) {
            throw new RobinbirdException(Msgs.get(JSON_PROCESSING_ISSUE, component.toString()), e);
        }
        return entity;
    }

    public static RelationEntity convert(@NonNull final Relation relation) {
        final RelationEntity entity = new RelationEntity();
        entity.setParentId(relation.getParent().getId());
        entity.setRelationCategory(relation.getRelationCategory().name());
        entity.setName(relation.getName());
        entity.setRelatedComponentId(relation.getRelatedComponent().getId());
        entity.setCardinality(relation.getCardinality().name());
        entity.setId(relation.getId());

        try {
            entity.setMetadata(objectMapper.writeValueAsString(relation.getMetadata()));
        } catch (final JsonProcessingException e) {
            throw new RobinbirdException(Msgs.get(JSON_PROCESSING_ISSUE, relation.toString()), e);
        }

        return entity;
    }

    public static Relation convert(@NonNull final RelationEntity entity,
                                   @NonNull final Component relatedComponent, @NonNull final Component parent) {
        final Map<String, String> metadata;
        try {
            metadata = objectMapper.readValue(entity.getMetadata(), new TypeReference<Map<String, String>>(){});
        } catch (final IOException e) {
            throw new RobinbirdException(Msgs.get(JSON_PROCESSING_ISSUE, entity.toString()), e);
        }
        return Relation.builder()
                       .name(Optional.ofNullable(entity.getName()).orElse(""))
                       .relationCategory(RelationCategory.valueOf(entity.getRelationCategory()))
                       .relatedComponent(relatedComponent)
                       .cardinality(Cardinality.valueOf(entity.getCardinality()))
                       .parent(parent)
                       .id(entity.getId())
                       .metadata(metadata)
                       .build();
    }
}
