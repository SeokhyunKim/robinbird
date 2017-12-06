package org.robinbird.graph.cluster;

/**
 * Created by seokhyun on 12/6/17.
 */
public class ScoreMatchers {

	public static boolean range(ClusterNode node, float minScore, float maxScore) {
		return (minScore <= node.getScore() && node.getScore() < maxScore);
	}
}
