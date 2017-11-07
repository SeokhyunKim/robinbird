package org.robinbird.code.model;

import lombok.Getter;

import java.util.List;

/**
 * Created by seokhyun on 6/2/17.
 */
@Getter
public class Collection extends Type {
	private List<Type> types;

	public Collection(String name, List<Type> types) {
		super(name, Kind.REFERENCE);
		this.types = types;
	}

	public String toString() {
		return "Collection: " + this.getName();
	}
}
