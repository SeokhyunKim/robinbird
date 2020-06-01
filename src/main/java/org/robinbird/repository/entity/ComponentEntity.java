package org.robinbird.repository.entity;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.Data;
import lombok.NonNull;

@Data
@Entity
@Table(name = "component",
       indexes = {
        @Index(columnList = "name"),
        @Index(columnList = "componentCategory"),
        @Index(columnList = "ownerId")})
public class ComponentEntity {

    @Id
    private String id;

    private String name;

    private String componentCategory;

    private String ownerId;

    private String metadata;

    public ComponentEntity() {
        this.id = UUID.randomUUID().toString();
    }

    public ComponentEntity(@NonNull final String id) {
        this.id = id;
    }

}
