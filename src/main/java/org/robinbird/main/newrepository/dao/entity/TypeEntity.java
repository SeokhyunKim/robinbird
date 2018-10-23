package org.robinbird.main.newrepository.dao.entity;

import static javax.persistence.FetchType.EAGER;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(indexes = {@Index(columnList = "name")})
public class TypeEntity {

    @Id
    @GeneratedValue
    private long id;

    private String name;

    private String category;

    @OneToMany(fetch = EAGER)
    @JoinColumn(name = "parentTypeId")
    List<InstanceEntity> instances;

    @OneToMany(fetch = EAGER)
    @JoinColumn(name = "parentTypeId")
    List<RelationEntity> relations;
    
}