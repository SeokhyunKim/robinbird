package org.robinbird.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by seokhyun on 6/2/17.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MemberFunction extends Member {
	private List<Type> arguments;

	public MemberFunction(AccessModifier accessModifier, Type type, String name) {
		super(accessModifier, type, name);
		arguments = new ArrayList<Type>();
	}

	public MemberFunction(AccessModifier accessModifier, Type type, String name, List<Type> arguments) {
		super(accessModifier, type, name);
		this.arguments = arguments;
	}
}
