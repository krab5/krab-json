package org.krab.json.path;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class ArrayIndices implements Iterable<Integer> {
	public static class Range implements Iterable<Integer>, Comparable<Range> {
		public final int lower, upper;
		
		public Range(int lower, int upper) {
			this.lower = lower;
			this.upper = upper;
			
			if (this.upper < this.lower)
				throw new IllegalArgumentException(String.format(
						"this range [%d-%d] would be empty"
						, lower, upper));
			if (this.lower < 0)
				throw new IllegalArgumentException("lower bound of range cannot be negative");
		}
		
		public Range(int value) {
			this(value, value);
		}
		
		@Override
		public int compareTo(Range other) {
			return Integer.compare(this.lower, other.lower);
		}
		
		public boolean isIn(int value) {
			return this.lower <= value && value <= this.upper;
		}
		
		public boolean intersects(Range other) {
			return this.isIn(other.lower) || this.isIn(other.upper);
		}
		
		public Range union(Range other) {
			if (!this.intersects(other))
				throw new IllegalArgumentException("can only union intersecting ranges (no disjoint ranges allowed)");
			return new Range(
					Math.min(this.lower, other.lower),
					Math.max(this.upper, other.upper));
		}
		
		public Range intersection(Range other) {
			return new Range(
					Math.max(this.lower, other.lower),
					Math.min(this.upper, other.upper));
		}
		
		@Override
		public Iterator<Integer> iterator() {
			return new Iterator<Integer> () {
				private int value = Range.this.lower;
				
				@Override
				public boolean hasNext() {
					return value <= Range.this.upper;
				}
				
				@Override
				public Integer next() {
					int tmp = this.value;
					this.value++;
					return tmp;
				}
			};
		}
		
		@Override
		public String toString() {
			if (this.lower == this.upper)
				return Integer.toString(this.lower);
			else
				return "%d-%d".formatted(this.lower, this.upper);
		}
		
		public static Range fromString(String input) {
			int dashid = input.indexOf('-');
			if (dashid < 0) { // Single number
				return new Range(Integer.parseInt(input));
			} else { // Complete range
				return new Range(
						Integer.parseInt(input.substring(0, dashid)),
						Integer.parseInt(input.substring(dashid + 1))
					);
			}
		}
	}
	
	private List<Range> parts = new ArrayList<>();
	
	public ArrayIndices(List<Range> parts) {
		this.parts.addAll(parts);
		this.normalize();
	}
	
	public ArrayIndices(Range... ranges) {
		this(List.of(ranges));
	}

	@Override
	public Iterator<Integer> iterator() {
		return new Iterator<Integer>() {
			private Iterator<Integer> current = null;
			private Iterator<Range> partIterator = parts.iterator();

			@Override
			public boolean hasNext() {
				return (current == null && partIterator.hasNext())
					|| (current != null && current.hasNext());
			}

			@Override
			public Integer next() {
				if (current == null) {
					current = partIterator.next().iterator();
				}
				Integer ret = current.next();
				if (!current.hasNext())
					current = null;
				return ret;
			}
			
		};
	}
	
	@Override
	public String toString() {
		return String.join(", ", this.parts.stream().map(Range::toString).toList());
	}
	
	public static ArrayIndices fromString(String input) {
		List<Range> parts = new ArrayList<>();
		
		for (String part : input.split(","))
			parts.add(Range.fromString(part));
		
		return new ArrayIndices(parts);
	}
	
	private void normalize() {
		this.parts.sort(Range::compareTo);
		int i = 0;
		
		while (i < this.parts.size()) {
			if (i + 1 < this.parts.size()) {
				if (this.parts.get(i).intersects(this.parts.get(i + 1))) { // Collapse
					Range surplus = this.parts.get(i + 1);
					this.parts.remove(i + 1);
					this.parts.set(i, this.parts.get(i).union(surplus));
					continue;
				}
			}
			
			i++;
		}
	}

}
