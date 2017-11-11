package org.robinbird.graph.analysis;

import org.robinbird.graph.model.Edge;
import org.robinbird.graph.model.Graph;
import org.robinbird.graph.model.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by seokhyun on 11/8/17.
 */
public class FloydAlgorithm {

	public static float[][] calculateDistances(Graph g) {
		List<Node> nodes = g.getNodes();
		int N = nodes.size();
		float[][] dist = new float[N][N];
		for ( int i=0; i<N; ++i ) {
			for (int j=0; j<N; ++j) {
				if (i == j) {
					dist[i][j] = 0;
				} else {
					dist[i][j] = Float.MAX_VALUE;
				}
			}
		}
		for(Node n : nodes) {
			for (Edge e : n.getEdges()) {
				int i = e.getSource().getId();
				int j = e.getTarget().getId();
				dist[i][j] = 1;
			}
		}
		for (int k=0; k<N; ++k) {
			for (int i=0; i<N; ++i) {
				for (int j=0; j<N; ++j) {
					if (dist[i][k] == Float.MAX_VALUE || dist[k][j] == Float.MAX_VALUE) {
						continue;
					}
					if (dist[i][j] > dist[i][k] + dist[k][j]) {
						dist[i][j] = dist[i][k] + dist[k][j];
					}
				}
			}
		}
		return dist;
	}

}
