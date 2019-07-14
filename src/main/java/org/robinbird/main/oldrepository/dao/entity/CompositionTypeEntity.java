package org.robinbird.main.oldrepository.dao.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode
@Table(name = "composition_type", indexes = {@Index(columnList = "typeId")})
public class CompositionTypeEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private long typeId;
    @Id
    private long compositionTypeId;

}
