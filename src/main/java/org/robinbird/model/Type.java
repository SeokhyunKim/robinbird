package org.robinbird.model;

import lombok.ToString;

/**
 * Created by seokhyun on 5/31/17.
 */
@ToString(callSuper = true)
public class Type extends Repositable {
	public Type(String name) {
		super(name);
	}
}
