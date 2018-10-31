package org.robinbird.main.newrepository.dao.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode
@Table(name = "instance", indexes = {@Index(columnList = "parentTypeId")})
public class InstanceEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private long parentTypeId;
    @Id
    private long typeId;
    @Id
    private String name;
    @Column
    private String accessModifier;
}
