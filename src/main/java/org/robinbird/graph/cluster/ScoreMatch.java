package org.robinbird.graph.cluster;

/**
 * Created by seokhyun on 11/12/17.
 */
public interface ScoreMatch {

	boolean match(ClusterNode node, float minScore, float maxScore);

}
