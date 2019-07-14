package org.robinbird.main.oldrepository2.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "relatedComponent", indexes = {@Index(columnList = "typeId")})
public class RelationEntity {

    @Id
    @GeneratedValue
    private long id;
    @Column
    private long typeId;
    @Column
    private String category;
    @Column
    private int cardinality;

}