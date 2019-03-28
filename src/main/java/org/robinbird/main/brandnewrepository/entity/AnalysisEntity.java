package org.robinbird.main.brandnewrepository.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(indexes = {@Index(columnList = "name")})
public class AnalysisEntity {

    @Id
    @GeneratedValue
    private long id;

    private String name;

    private String category;

}
