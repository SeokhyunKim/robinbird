package org.robinbird.main.newrepository.dao.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode
@Table(name = "type", indexes = {@Index(columnList = "name")})
public class TypeEntity {

    @Id
    @GeneratedValue
    private long id;

    private String name;

    private String category;
    
}