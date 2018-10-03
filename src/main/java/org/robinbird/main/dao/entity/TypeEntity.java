package org.robinbird.main.dao.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "type")
public class TypeEntity extends RobinbirdEntity {

    private String kind;

    private boolean varargs;

}
