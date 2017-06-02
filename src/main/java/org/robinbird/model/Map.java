package org.robinbird.model;

import lombok.Getter;
import lombok.ToString;

/**
 * Created by seokhyun on 6/2/17.
 */
@Getter
@ToString(callSuper = true)
public class Map extends Type {
	private Type key, value;

	public Map(String name, Type key, Type value) {
		super(name);
		this.key = key;
		this.value = value;
	}
}
