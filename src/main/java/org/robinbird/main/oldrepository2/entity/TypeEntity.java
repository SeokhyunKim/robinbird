package org.robinbird.main.oldrepository2.entity;

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
@Table(name = "type", indexes = {@Index(columnList = "name")})
public class TypeEntity {

    @Id
    @GeneratedValue
    private long id;

    private String name;

    private String category;

}