package org.robinbird.main.repository.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import lombok.Data;

@Data
@Entity
public class Relation {

    @EmbeddedId
    private RelationKey key;

    private String relationType;

    private int cardinality;

}
