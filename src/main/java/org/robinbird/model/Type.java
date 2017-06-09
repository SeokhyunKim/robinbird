package org.robinbird.model;

import lombok.Getter;
import lombok.ToString;

import static org.robinbird.model.Type.Kind.REFERENCE;

/**
 * Created by seokhyun on 5/31/17.
 */
@ToString(callSuper = true)
public class Type extends Repositable {

	public enum Kind { PRIMITIVE, REFERENCE, ARRAY };

	@Getter private Kind kind;

	public Type(String name) {
		super(name);
		kind = REFERENCE;
	}

	public Type(String name, Kind kind) {
		super(name);
		this.kind = kind;
	}

}
