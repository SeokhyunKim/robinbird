package org.robinbird.main.newrepository.dao.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Data;

@Data
@Entity
@Builder
@Table(indexes = {@Index(columnList = "typeId")})
public class CompositionTypeRelation {

    @Id
    private long typeId;

    @Id
    private long compositionTypeId;

}
