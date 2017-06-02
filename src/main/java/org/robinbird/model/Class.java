package org.robinbird.model;

import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by seokhyun on 6/2/17.
 */
@Getter
@ToString(callSuper = true)
public class Class extends Repositable {

	private ClassType classType;
	private Map<String, Member> memberVariables;
	private Map<String, MemberFunction> memberFunctions;

	public Class(String name) {
		super(name);
		classType = ClassType.CLASS;
		this.memberVariables = new HashMap<String, Member>();
		this.memberFunctions = new HashMap<String, MemberFunction>();
	}

	public Class(ClassType classType, String name) {
		this(name);
		this.classType = classType;
	}
}
