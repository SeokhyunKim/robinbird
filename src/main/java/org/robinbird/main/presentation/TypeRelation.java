package org.robinbird.main.presentation;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;
import org.robinbird.main.oldmodel2.Type;

@Getter
@ToString
public class TypeRelation {

	@Value
	public static class Key {
		String first, second;
	}

	private final Type first, second;
	private String firstCardinality, secondCardinality;
	
	public Key getKey() {
		return new Key(first.getName(), second.getName());
	}

	private TypeRelation(Type first, Type second) {
		this.first = first;
		this.second = second;
	}

	public void updateCardinality(@NonNull final Type associated, @NonNull String cardinality) {
		if (first.getName().equals(associated)) {
			this.firstCardinality = cardinality;
		} else if (second.getName().equals(associated.getName())) {
			this.secondCardinality = cardinality;
		}
	}

	public static TypeRelation create(@NonNull final Type first, @NonNull final Type second, @NonNull final String cardinality) {
		if (first.compareTo(second) <= 0) {
			return new TypeRelation(first, second);
		} else {
			return new TypeRelation(second, first);
		}
	}

	public static Key createKey(@NonNull final Type first, @NonNull final Type second) {
		if (first.compareTo(second) <= 0) {
			return new Key(first.getName(), second.getName());
		} else {
			return new Key(second.getName(), first.getName());
		}
	}

	public static Key createKey(@NonNull final String first, @NonNull final String second) {
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
