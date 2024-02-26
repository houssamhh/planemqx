package planemqx.common;

import java.util.Objects;

public class Pair<K, V> {

	private K first;
	private V second;

	private Pair(K first, V second) {
		this.first = first;
		this.second = second;
	}

	@Override
	public int hashCode() {
		return Objects.hash(first, second);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Pair<?, ?> other = (Pair<?, ?>) obj;
		return Objects.equals(first, other.first) && Objects.equals(second, other.second);
	}

	@Override
	public String toString() {
		return "Pair [first=" + first + ", second=" + second + "]";
	}

	public K getFirst() {
		return first;
	}

	public void setFirst(final K first) {
		this.first = first;
	}

	public V getSecond() {
		return second;
	}

	public void setSecond(final V second) {
		this.second = second;
	}

	public static <K, V> Pair<K, V> of(final K first, final V second) {
		return new Pair<>(first, second);
	}
}
