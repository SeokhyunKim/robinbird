package org.robinbird.clustering;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NodeDistance {

    public static final NodeDistance INFINITE = new NodeDistance(true, Double.MAX_VALUE);
    public static final NodeDistance ZERO = new NodeDistance(false, 0.0);

    final boolean isInfinite;
    final double distance;

    public NodeDistance(double distance) {
        this.isInfinite = false;
        this.distance = distance;
    }

    public boolean isInfinite() {
        return isInfinite;
    }

    public double getDistance() {
        if (isInfinite) {
            return Double.MAX_VALUE;
        }
        return this.distance;
    }

    public NodeDistance plus(double value) {
        if (this.isInfinite) {
            return NodeDistance.INFINITE;
        }
        return new NodeDistance(this.distance + value);
    }

    public NodeDistance plus(NodeDistance dist) {
        if (this.isInfinite || dist.isInfinite) {
            return NodeDistance.INFINITE;
        }
        return new NodeDistance(this.distance + dist.getDistance());
    }

    public boolean greaterThan(NodeDistance... distances) {
        if (this.isInfinite()) {
            return false;
        }
        double value = 0.0;
        for (NodeDistance dist : distances) {
            if (dist.isInfinite) {
                return false;
            }
            value += dist.getDistance();
        }
        return this.distance > value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof NodeDistance)) {
            return false;
        }
        NodeDistance other = (NodeDistance) o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.isInfinite() && other.isInfinite()) {
            return true;
        } else if ((!this.isInfinite() && other.isInfinite()) || (this.isInfinite() && !other.isInfinite())) {
            return false;
        }
        return Double.compare(this.distance, other.distance) == 0;
    }

    @Override public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME;
        if (!this.isInfinite()) {
            final long temp = Double.doubleToLongBits(this.getDistance());
            result = result * PRIME + (int)(temp ^ (temp >>>32));
        }
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof NodeDistance;
    }
}
