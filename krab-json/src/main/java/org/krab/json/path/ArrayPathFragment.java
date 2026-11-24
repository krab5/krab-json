package org.krab.json.path;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.krab.json.JsonArray;
import org.krab.json.JsonValue;

final class ArrayPathFragment implements PathFragment {
	// /[<indices>]
	private final Optional<ArrayIndices> arrayIndices;
	
	public ArrayPathFragment (Optional<ArrayIndices> arrayIndices) {
		this.arrayIndices = arrayIndices;
	}

	@Override
	public Collection<? extends JsonValue> matches(JsonValue value) {
		List<JsonValue> result = new ArrayList<>();
		
		if (value instanceof JsonArray arr) {
			Iterable<Integer> indices = this.arrayIndices.isPresent()
					? this.arrayIndices.get()
					: new ArrayIndices.Range(0, arr.getSize());
					
			for (int i : indices) {
				if (i < arr.getSize())
					result.add(arr.get(i));
			}
		}
		
		return result;
	}
	
	public static ArrayPathFragment fromString(String input) {
		if (input.charAt(0) != '[' || input.charAt(input.length() - 1) != ']') {
			throw new IllegalArgumentException("array-type fragments must be of the formt [...]");
		}
		
		if (input.equals("[*]"))
			return new ArrayPathFragment(Optional.empty());
		else
			return new ArrayPathFragment(
					Optional.of(ArrayIndices.fromString(input.substring(1, input.length() - 1))));
	}
	
	@Override
	public String toString() {
		if (this.arrayIndices.isPresent())
			return "[" + this.arrayIndices.get().toString() + "]";
		else
			return "[*]";
	}
}
