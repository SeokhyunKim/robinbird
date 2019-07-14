package org.robinbird.main.oldmodel;

import lombok.Getter;
import org.robinbird.util.Msgs;

import java.util.List;

import static com.google.common.base.Preconditions.checkState;
import static org.robinbird.util.Msgs.Key.FAILED_TO_GET_ASSOCIATED_TYPE_FROM_COLLECTION;

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

	public Type getAssociatedType() {
		checkState(types.size() > 0, Msgs.get(FAILED_TO_GET_ASSOCIATED_TYPE_FROM_COLLECTION, getName()));
		return types.get(types.size()-1);
	}

	public String toString() {
		return "Collection: " + this.getName();
	}
}