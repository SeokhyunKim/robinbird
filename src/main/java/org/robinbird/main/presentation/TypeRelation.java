package org.robinbird.main.presentation;

import static com.google.common.base.Preconditions.checkState;

import org.robinbird.main.model.Type;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.Value;

@Getter
@ToString
public class TypeRelation {

	@Value
	public static class Key {
		String first, second;
	}

	Type first, second;
	@Setter String firstCardinality, secondCardinality;
	
	public Key getKey() {
		return new Key(first.getName(), second.getName());
	}

	private TypeRelation(Type first, Type second) {
		this.first = first;
		this.second = second;
	}	

	public static TypeRelation create(Type first, Type second) {
		checkState(first != null);
		checkState(second != null);
		if (first.compareTo(second) <= 0) {
			return new TypeRelation(first, second);
		} else {
			return new TypeRelation(second, first);
		}
	}

	public static Key createKey(Type first, Type second) {
		checkState(first != null);
		checkState(second != null);
		if (first.compareTo(second) <= 0) {
			return new Key(first.getName(), second.getName());
		} else {
			return new Key(second.getName(), first.getName());
		}
	}

	public static Key createKey(String first, String second) {
		checkState(first != null);
		checkState(second != null);
		String former, later;
		if (first.compareTo(second) <= 0) {
			former = first;
			later = second;
		} else {
			former = second;
			later = first;
		}
		return new Key(former, later);
	}

}
