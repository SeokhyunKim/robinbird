package org.robinbird.model;

import lombok.Getter;
import lombok.Setter;

import static org.robinbird.model.Type.Kind.PRIMITIVE;
import static org.robinbird.model.Type.Kind.REFERENCE;

/**
 * Created by seokhyun on 5/31/17.
 */
@Getter
public class Type extends Repositable {

	public enum Kind { PRIMITIVE, REFERENCE};

	private Kind kind;
	@Setter private boolean varargs;

	public Type(String name, Kind kind) {
		super(name);
		this.kind = kind;
		this.varargs = false;
	}

	public String toString() {
		return "Type: " + this.getName();
	}

	public boolean isPrimitiveType() {
		return kind == PRIMITIVE;
	}

	public boolean isReferenceType() {
		return kind == REFERENCE;
	}

	public int compareTo(Type another) {
		return this.getName().compareTo(another.getName());
	}

}
