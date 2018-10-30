package org.robinbird.main.newrepository.dao.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "composition_type", indexes = {@Index(columnList = "typeId")})
public class CompositionTypeEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private long typeId;
    @Id
    private long compositionTypeId;

}
