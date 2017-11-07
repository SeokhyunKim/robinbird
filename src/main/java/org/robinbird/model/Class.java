package org.robinbird.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by seokhyun on 6/2/17.
 */
@Getter
public class Class extends Type {

	@Setter private ClassType classType = ClassType.CLASS;
	@Setter private Class parent;
	@Setter private Package classPackage;
	private List<Class> interfaces = new ArrayList<>();
	private TreeMap<String, Member> memberVariables = new TreeMap<>();
	private TreeMap<String, MemberFunction> memberFunctions = new TreeMap<>();

	public Class(String name) {
		super(name, Kind.REFERENCE);
	}

	public Class(String name, ClassType classType) {
		this(name);
		this.classType = classType;
	}

	public String getFullName() {
		if (classPackage != null) {
			return classPackage.getName() + "." + getName();
		}
		return getName();
	}

	public String toString() {
		return classType.toString() + ": " + this.getName();
	}

	public void addInterface(Class interfaceClass) {
		// TO DO: check just doing this is safe
		//checkState(interfaceClass.getClassType() == ClassType.INTERFACE);
		interfaces.add(interfaceClass); }

	public void addMember(Member m) {
		checkState(!m.getName().isEmpty());
		memberVariables.put(m.getName(), m);
	}

	public void addMemberFunction(MemberFunction f) {
		checkState(!f.getSignature().isEmpty());
		memberFunctions.put(f.getSignature(), f);
	}

	public MemberFunction getMemberFunction(String signature) {
		return memberFunctions.get(signature);
	}
}
