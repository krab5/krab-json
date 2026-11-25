package org.krab.json.path;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.krab.json.JsonObject;
import org.krab.json.JsonValue;

final class ObjectPathFragment implements PathFragment {
	// /<key>
	private final StringValueMatcher keyMatcher;
	
	public ObjectPathFragment(StringValueMatcher keyMatcher) {
		this.keyMatcher = keyMatcher;
	}
	
	@Override
	public Collection<? extends JsonValue> matches(JsonValue value) {
		List<JsonValue> result = new ArrayList<>();
		if (value instanceof JsonObject obj) {
			for (String k : obj.keyList()) {
				if (this.keyMatcher.matches(k))
					result.add(obj.get(k));
			}
		}
		
		return result;
	}
	
	@Override
	public String toString() {
		return this.keyMatcher.toString();
	}
	
	public static ObjectPathFragment fromString(String input) {
		return new ObjectPathFragment(StringValueMatcher.fromString(input, false));
	}
}
