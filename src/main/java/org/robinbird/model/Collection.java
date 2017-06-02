package org.robinbird.model;

import lombok.Getter;
import lombok.ToString;

/**
 * Created by seokhyun on 6/2/17.
 */
@Getter
@ToString(callSuper = true)
public class Collection extends Type {
	private Type type;

	public Collection(String name, Type type) {
		super(name);
		this.type = type;
	}
}
