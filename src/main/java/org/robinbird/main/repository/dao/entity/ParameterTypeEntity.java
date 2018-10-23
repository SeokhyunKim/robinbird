package org.robinbird.main.repository.dao.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Data;

@Data
@Entity
public class ParameterTypeEntity {

    @Id
    @GeneratedValue
    private long id;

    private long typeId;
    boolean varargs;
}
