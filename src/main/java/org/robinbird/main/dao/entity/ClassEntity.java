package org.robinbird.main.dao.entity;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
public class ClassEntity extends RobinbirdEntity {

    private String classType;
    private long parentId;
    private long packageId;

    @OneToMany
    List<ClassEntity> interfaces;




    private boolean varargs;

    /*
    @Setter private ClassType classType = ClassType.CLASS;
	@Setter private Class parent;
	@Setter private Package classPackage;
	private List<Class> interfaces = new ArrayList<>();
	private TreeMap<String, Member> memberVariables = new TreeMap<>();
	private TreeMap<String, MemberFunction> memberFunctions = new TreeMap<>();
     */
}
