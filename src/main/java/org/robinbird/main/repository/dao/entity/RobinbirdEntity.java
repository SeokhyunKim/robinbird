package org.robinbird.main.repository.dao.entity;

import static javax.persistence.InheritanceType.JOINED;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.Table;
import lombok.Data;


@Data
@Entity
@Table(indexes = {@Index(columnList = "name")})
@Inheritance(strategy = JOINED)
public class RobinbirdEntity {

    @Id
    @GeneratedValue
    private long id;

    private String name;

    private String type;
}
