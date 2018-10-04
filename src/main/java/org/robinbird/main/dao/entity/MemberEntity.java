package org.robinbird.main.dao.entity;

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

    private String accessModifier;
    private String description;


}
