package org.robinbird.main.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import static org.robinbird.main.model.Type.Kind.PRIMITIVE;
import static org.robinbird.main.model.Type.Kind.REFERENCE;

/**
 * Created by seokhyun on 5/31/17.
 */
@Getter
public class Type extends RobinbirdObject {

	public enum Kind {PRIMITIVE, REFERENCE};

	private Kind kind;

	public Type(String name, Kind kind) {
		super(name);
		this.kind = kind;
	}

	@Builder
	public Type(final long id, @NonNull final String name, @NonNull final Kind kind, final boolean varargs) {
		super(id, name);
		this.kind = kind;
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
