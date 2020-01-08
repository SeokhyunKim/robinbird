package org.robinbird.repository.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@IdClass(RelationEntityKey.class)
@NoArgsConstructor
@Table(name = "relation", indexes = {@Index(columnList = "parentId")})
public class RelationEntity {

    @Id
    private String parentId;

    @Id
    private String id;

    private String relationCategory;

    private String name;

    private String relatedComponentId;

    private String cardinality;

    private String metadata;

}
