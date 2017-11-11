package org.robinbird.common.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Created by seokhyun on 11/8/17.
 */
public class Pair<T> implements Comparable<Pair> {
	@Getter
	private T first, second;
	@Getter @Setter
	private float valueForSorting;

	public Pair(@NonNull final T p1, @NonNull final T p2) {
		this.first = p1;
		this.second = p2;
	}

	public Pair(@NonNull final T p1, @NonNull final T p2, float value) {
		this(p1, p2);
		setValueForSorting(value);
	}

	public int compareTo(Pair p) {
		if (this.valueForSorting == p.getValueForSorting()) {
			return 0;
		}
		if (this.valueForSorting < p.getValueForSorting()) {
			return -1;
		}
		return 1;
	}


}
