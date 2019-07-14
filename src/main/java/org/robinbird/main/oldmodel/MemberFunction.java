package org.robinbird.main.oldmodel;

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

	private final List<ParameterType> parameters;

	public MemberFunction(AccessModifier accessModifier, Type type, String name) {
		super(accessModifier, type, name);
		parameters = new ArrayList<>();
	}

	public MemberFunction(AccessModifier accessModifier, Type type, String name, List<ParameterType> parameters) {
		super(accessModifier, type, name);
		this.parameters = parameters;
	}

	public String getSignature() {
		return MemberFunction.createMethodSignature(getName(), parameters);
	}

	public static String createMethodSignature(String methodName, List<ParameterType> params) {
		String signature = methodName;
		if (params != null) {
			for (ParameterType t : params) {
				signature += "_" + t.getType().getName();
				if (t.isVarargs()) {
					signature += "varargs";
				}
			}
		}
		return signature;

	}
}
