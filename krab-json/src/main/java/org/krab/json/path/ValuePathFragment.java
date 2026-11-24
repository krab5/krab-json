package org.krab.json.path;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.krab.json.JsonValue;

final class ValuePathFragment implements PathFragment {
	// /=<value>::<type>
	private final ValueMatcher valueMatcher;
	private final FragmentType fragmentType;
	
	public ValuePathFragment(ValueMatcher valueMatcher, FragmentType fragmentType) {
		this.valueMatcher = valueMatcher;
		this.fragmentType = fragmentType;
	}

	@Override
	public Collection<? extends JsonValue> matches(JsonValue value) {
		List<JsonValue> result = new ArrayList<>();
		
		if (this.fragmentType.checkType(value) && this.valueMatcher.matches(value))
			result.add(value);
		
		return result;
	}
	
	@Override
	public String toString() {
		return "=" + this.valueMatcher.toString() + "::" + this.fragmentType.toString();
	}
	
	public static ValuePathFragment fromString(String input) {
		FragmentType type = FragmentType.ANY_TYPE;
		int typebegin = input.indexOf("::");
		if (typebegin >= 0)
			type = FragmentType.fromString(input.substring(typebegin + 2));
		
		ValueMatcher matcher = new AnyValueMatcher(type);
		if (input.charAt(0) == '=') {
			String valuestr = input.substring(1, typebegin >= 0 ? typebegin : input.length());
			matcher = ValueMatcher.fromString(valuestr, type);
		}
		
		return new ValuePathFragment(matcher, type);
	}
}
