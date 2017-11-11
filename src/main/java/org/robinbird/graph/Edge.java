package org.robinbird.graph;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

/**
 * Created by seokhyun on 11/7/17.
 */
@Getter
@EqualsAndHashCode
public class Edge {
	final Node source, target;

	Edge(@NonNull final Node source, @NonNull final Node target) {
		this.source = source;
		this.target = target;
	}
}
