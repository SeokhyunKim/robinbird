package org.robinbird.main.oldmodel;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by seokhyun on 6/2/17.
 */
@Getter
@ToString
public class RobinbirdObject {

	@Setter  protected long id; // todo: setter can be removed after migration to h2 db is completed
	@NonNull final protected String name;

	public RobinbirdObject(final long id, @NonNull final String name) {
		this.id = id;
		this.name = name;
	}

	public RobinbirdObject(@NonNull final String name) {
		this.name = name;
	}

	@Override
	final public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof RobinbirdObject)) return false;
		RobinbirdObject other = (RobinbirdObject) o;
		return this.getName().equals(other.getName());
	}

	@Override
	final public int hashCode() {
		return this.getName().hashCode();
	}

}
