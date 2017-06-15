package org.robinbird.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by seokhyun on 6/2/17.
 */
@Getter
@ToString(callSuper = true)
public class Class extends Repositable {

	private ClassType classType = ClassType.CLASS;
	@Setter private Class parent;
	private List<Class> interfaces = new ArrayList<>();
	private Map<String, Member> memberVariables = new HashMap<>();
	private TreeMap<String, MemberFunction> memberFunctions = new TreeMap<>();

	public Class(String name) {
		super(name);
	}

	public Class(String name, ClassType classType) {
		this(name);
		this.classType = classType;
	}

	public void addInterface(Class interfaceClass) { interfaces.add(interfaceClass); }

	public void addMember(Member m) {
		memberVariables.put(m.getName(), m);
	}

	public void addMemberFunction(MemberFunction f) {
		memberFunctions.put(f.getName(), f);
	}
}
