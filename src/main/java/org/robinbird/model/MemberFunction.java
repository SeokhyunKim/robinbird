package org.robinbird.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by seokhyun on 6/2/17.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class MemberFunction extends Member {

	private final List<Type> arguments;

	public MemberFunction(AccessModifier accessModifier, Type type, String name) {
		super(accessModifier, type, name);
		arguments = new ArrayList<>();
	}

	public MemberFunction(AccessModifier accessModifier, Type type, String name, List<Type> arguments) {
		super(accessModifier, type, name);
		this.arguments = arguments;
	}
}
