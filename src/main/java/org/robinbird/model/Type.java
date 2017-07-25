package org.robinbird.model;

import lombok.Getter;

import static org.robinbird.model.Type.Kind.PRIMITIVE;
import static org.robinbird.model.Type.Kind.DEFINED;

/**
 * Created by seokhyun on 5/31/17.
 */
public class Type extends Repositable {

	public enum Kind { PRIMITIVE, DEFINED};

	@Getter private Kind kind;

	public Type(String name, Kind kind) {
		super(name);
		this.kind = kind;
	}

	public String toString() {
		return "Type: " + this.getName();
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
