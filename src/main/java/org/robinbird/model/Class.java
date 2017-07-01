package org.robinbird.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by seokhyun on 6/2/17.
 */
@Getter
@ToString(callSuper = true)
public class Class extends Type {

	@Setter private ClassType classType = ClassType.CLASS;
	@Setter private Class parent;
	private List<Class> interfaces = new ArrayList<>();
	private TreeMap<String, Member> memberVariables = new TreeMap<>();
	private TreeMap<String, MemberFunction> memberFunctions = new TreeMap<>();

	public Class(String name) {
		super(name);
	}

	public Class(String name, ClassType classType) {
		this(name);
		this.classType = classType;
	}

	public void addInterface(Class interfaceClass) {
		checkState(interfaceClass.getClassType() == ClassType.INTERFACE);
		interfaces.add(interfaceClass); }

	public void addMember(Member m) {
		checkState(m.getName() != null);
		memberVariables.put(m.getName(), m);
	}

	public void addMemberFunction(MemberFunction f) {
		checkState(f.getName() != null);
		memberFunctions.put(f.getName(), f);
	}
}
