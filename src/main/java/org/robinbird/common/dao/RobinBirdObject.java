package org.robinbird.common.dao;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by seokhyun on 6/2/17.
 */
@Getter
@ToString
@RequiredArgsConstructor
public class RobinBirdObject {

	@Setter  protected int id;
	@NonNull final protected String name;

	@Override
	final public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof RobinBirdObject)) return false;
		RobinBirdObject other = (RobinBirdObject) o;
		return this.getName().equals(other.getName());
	}

	@Override
	final public int hashCode() {
		return this.getName().hashCode();
	}

}
