package org.robinbird.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.Value;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by seokhyun on 6/10/17.
 */
@Getter
@ToString
public class Relation {

	@Value
	public static class Key {
		String first, second;
	}

	Type first, second;
	@Setter String firstCardinality, secondCardinality;

	private Relation(Type first, Type second) {
		this.first = first;
		this.second = second;
	}

	public Key getKey() {
		return new Key(first.getName(), second.getName());
	}

	public static Relation create(Type first, Type second) {
		checkState(first != null);
		checkState(second != null);
		if (first.compareTo(second) <= 0) {
			return new Relation(first, second);
		} else {
			return new Relation(second, first);
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
