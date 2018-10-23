package org.robinbird.main.repository.dao.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Data;

@Data
@Entity
public class MemberEntity {

    @Id
    @GeneratedValue
    private long id;

    private long classId;

    private String accessModifier;
    private long typeId;
    private String name;
}
