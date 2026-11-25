package org.krab.json.path;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.krab.json.JsonValue;

///
/// A JSON path is constituted of _fragments_. Each fragment represents
/// a filter on a set of possible outcomes. Fragments are of three kinds:
///  - object fragments of the form `<key>::<type>`
///  - array fragments of the form `[<indices>]::<type>`
///  - value fragments of the form `=<value>::<type>`
/// 
/// <key> is string, <type> == string, integer, decimal, boolean (optional)
/// <indices> ::= \d-\d, \d, * (match all)
/// <value> ::= [\d-\d] if integer, * matches all
/// strings can be regex'd: |<regex>|
public class JsonPath {
	private static class Path {
		private List<PathFragment> fragments = new ArrayList<>();
		
		public Path(List<PathFragment> fragments) {
			this.fragments.addAll(fragments);
		}
		
		public void advance() {
			if (!this.fragments.isEmpty())
				this.fragments.removeFirst();
		}
		
		public Optional<PathFragment> head() {
			if (!this.fragments.isEmpty())
				return Optional.of(this.fragments.getFirst());
			else
				return Optional.empty();
		}
		
		public boolean isEmpty() {
			return this.fragments.isEmpty();
		}
		
		public Path copy() {
			List<PathFragment> fragments = new ArrayList<>();
			fragments.addAll(this.fragments);
			return new Path(fragments);
		}
		
		@Override
		public String toString() {
			return this.fragments.toString();
		}
	}
	
	private final Path path;
	
	JsonPath(List<PathFragment> fragments) {
		this.path = new Path(fragments);
	}
	
	public Set<? extends JsonValue> filter(Collection<? extends JsonValue> values) {
		Set<JsonValue> result = new HashSet<>(values);
		Path front = this.path.copy();
		
		while (!result.isEmpty() && !front.isEmpty()) {
			PathFragment head = front.head().get();
			front.advance();
			
			Set<JsonValue> tmp = new HashSet<>();
			
			for (JsonValue value : result)
				tmp.addAll(head.matches(value));
			
			result.clear();
			result.addAll(tmp);
		}
		
		return result;
	}
	
	public Set<? extends JsonValue> filter(JsonValue value) {
		return this.filter(List.of(value));
	}
	
	@Override
	public String toString() {
		return this.path.toString();
	}
	
	public static Set<? extends JsonValue> filter(JsonValue value, JsonPath path) {
		return path.filter(value);
	}
	
	public static Set<? extends JsonValue> filter(JsonValue value, String path) {
		return JsonPath.makePath(path).filter(value);
	}
	
	public static JsonPath makePath(List<String> fragments) {
		List<PathFragment> fragmentList = new ArrayList<>();
		
		for (String frag : fragments)
			fragmentList.add(PathFragment.fromString(frag));
		
		return new JsonPath(fragmentList);
	}
	
	public static JsonPath makePath(String... fragments) {
		return makePath(List.of(fragments));
	}
	
	public static JsonPath makePath(String path) {
		List<String> fragments = new ArrayList<>();
		int i = 0;
		
		while (i < path.length()) {
			int tmp = Utils.nextUnescapedOutRegex(path, i, '/');
			
			if (tmp == i) {
				i++;
			} else {
				fragments.add(Utils.unescape(path.substring(i, tmp), '/'));
				i = tmp;
			}
		}
		
		return makePath(fragments);
	}
	
	
}
