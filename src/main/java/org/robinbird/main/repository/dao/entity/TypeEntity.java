package org.robinbird.main.repository.dao.entity;

import javax.persistence.Entity;
import lombok.Data;

@Data
@Entity
public class TypeEntity extends RobinbirdEntity {

    private String kind;

}
