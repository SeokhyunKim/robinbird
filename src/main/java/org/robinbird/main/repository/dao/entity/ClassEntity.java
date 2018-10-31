package org.robinbird.main.repository.dao.entity;

import static javax.persistence.FetchType.EAGER;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import lombok.Data;

@Data
@Entity
public class ClassEntity extends RobinbirdEntity {

    private String classType;
    private long parentId;
    private long packageId;

    @OneToMany(fetch = EAGER)
    private List<ClassEntity> interfaces;

    @OneToMany(fetch = EAGER)
    private List<MemberEntity> memberVariables;

    @OneToMany(fetch = EAGER)
    private List<MemberFunctionEntity> memberFunctions;
}
