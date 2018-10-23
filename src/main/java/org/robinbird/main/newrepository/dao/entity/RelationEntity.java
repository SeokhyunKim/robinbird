package org.robinbird.main.newrepository.dao.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(indexes = {@Index(columnList = "parentTypeId")})
public class RelationEntity {

    @Id
    @GeneratedValue
    private long id;

    private long parentTypeId;
    private long typeId;
    private String category;
}
