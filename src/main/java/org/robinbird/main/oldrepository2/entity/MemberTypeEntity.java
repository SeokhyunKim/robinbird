package org.robinbird.main.oldrepository2.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "member_type")
public class MemberTypeEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private long memberId;
    @Id
    private long memberTypeId;
    @Column
    private int order;

}