package org.robinbird.repository.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(indexes = {@Index(columnList = "name")})
public class AnalysisEntity {

    @Id
    @GeneratedValue
    private long id;

    private String name;

    private String category;

}
