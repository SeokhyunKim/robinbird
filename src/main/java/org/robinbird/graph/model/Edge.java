package org.robinbird.graph.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import org.robinbird.common.model.Repositable;

/**
 * Created by seokhyun on 11/7/17.
 */
@Getter
@EqualsAndHashCode
public class Edge {
	Node source, target;

	Edge(@NonNull final Node source, @NonNull final Node target) {
		this.source = source;
		this.target = target;
	}
}
