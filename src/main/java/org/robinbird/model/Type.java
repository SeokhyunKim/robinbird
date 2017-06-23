package org.robinbird.model;

import lombok.Getter;
import lombok.ToString;

import static org.robinbird.model.Type.Kind.PRIMITIVE;
import static org.robinbird.model.Type.Kind.DEFINED;

/**
 * Created by seokhyun on 5/31/17.
 */
@ToString(callSuper = true)
public class Type extends Repositable {

	public enum Kind { PRIMITIVE, DEFINED};

	@Getter private Kind kind;

	public Type(String name) {
		super(name);
		kind = DEFINED;
	}

	public Type(String name, Kind kind) {
		super(name);
		this.kind = kind;
	}

	public boolean isPrimitiveType() {
		return kind == PRIMITIVE;
	}

	public boolean isDefinedType() {
		return kind == DEFINED;
	}

	public int compareTo(Type another) {
		return this.getName().compareTo(another.getName());
	}

}
