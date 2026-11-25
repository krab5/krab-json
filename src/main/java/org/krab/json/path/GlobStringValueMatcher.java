package org.krab.json.path;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class GlobStringValueMatcher extends StringValueMatcher {
	private final boolean globAtStart, globAtEnd;
	private final List<String> fragments = new ArrayList<>();
	
	public GlobStringValueMatcher(boolean globAtStart, boolean globAtEnd, List<String> fragments) {
		this.fragments.addAll(fragments);
		this.globAtStart = globAtStart;
		this.globAtEnd = globAtEnd;
	}
	
	public GlobStringValueMatcher(boolean globAtStart, boolean globAtEnd, String... fragments) {
		this(globAtStart, globAtEnd, Arrays.asList(fragments));
	}

	@Override
	public boolean matches(String input) {
		boolean result = true;
		boolean globing = this.globAtStart;
		int id = 0, fragment = 0;
		
		while (result && id < input.length() && fragment < this.fragments.size()) {
			if (globing) {
				globing = !matchFragment(this.fragments.get(fragment), input, id);
				
				if (globing)
					id++;
			} else {
				result = matchFragment(this.fragments.get(fragment), input, id);
				
				if (result) {
					id += this.fragments.get(fragment).length();
					fragment++;
					globing = true;
				}
			}
		}
		
		result = result && (fragment >= this.fragments.size()) &&
				(this.globAtEnd || id >= input.length());

		return result;
	}
	
	@Override
	public String toString() {
		return (this.globAtStart ? "*" : "")
			 + String.join("*", this.fragments)
			 + (this.globAtEnd ? "*" : "");
	}
	
	private static boolean matchFragment(String fragment, String input, int id) {
		return id + fragment.length() <= input.length() &&
			fragment.equals(input.substring(id, id + fragment.length()));
	}
	
	public static GlobStringValueMatcher fromString(String input) {
		List<String> fragments = new ArrayList<>();
		
		boolean globStart = false, globEnd = false;
		boolean skipNext = false;
		int i = 0, checkpoint = 0;
		
		for (i = 0; i < input.length(); i++) {
			if (!skipNext && input.charAt(i) == '*') {
				if (i != checkpoint)
					fragments.add(input.substring(checkpoint, i));
				checkpoint = i + 1;
				skipNext = false;
				
				if (i == 0) globStart = true;
				if (i == input.length() - 1) globEnd = true;
			} else if (!skipNext && input.charAt(i) == '\\') {
				skipNext = true;
			} else if (skipNext) {
				skipNext = false;
			}
		}
		
		if (checkpoint < input.length())
			fragments.add(input.substring(checkpoint));
		
		return new GlobStringValueMatcher(globStart, globEnd, 
				fragments.stream().map(x -> x.replace("\\*", "*")).toList() // de-escape
			);
	}
	
}
