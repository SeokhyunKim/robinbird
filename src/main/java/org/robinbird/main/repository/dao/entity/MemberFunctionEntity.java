package org.robinbird.main.repository.dao.entity;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.Data;

@Data
@Entity
public class MemberFunctionEntity {

    @Id
    @GeneratedValue
    private long id;

    private long classId;

    private String accessModifier;
    private long typeId;
    private String name;

    @OneToMany
    private List<ParameterTypeEntity> parameters;
}
