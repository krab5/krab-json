package org.krab.json.path;

abstract class Utils {
	private Utils() {}
	
	
	public static int nextUnescapedOutRegex(String input, int begin, char target) {
		int i;
		boolean skipNext = false, isRegex = false;
		for (i = begin; i < input.length() && (skipNext || isRegex || input.charAt(i) != target); i++) {
			if (input.charAt(i) == '\\') {
				skipNext = !skipNext;
				continue;
			}
			
			if (!skipNext && input.charAt(i) == '!') {
				isRegex = !isRegex;
			}
			
			skipNext = false;
		}
		
		return i;
	}
	
	public static String unescape(String input, char... chars) {
		String output = "";
		boolean slash = false;
		int i = 0;
		
		while (i < input.length()) {
			if (!slash && input.charAt(i) == '\\') {
				slash = true;
			} else if (slash) {
				boolean unescaped = false;
				for (char c : chars) {
					if (input.charAt(i) == c) {
						output += c;
						unescaped = true;
					}
				}
				
				if (!unescaped)
					output += "\\" + input.charAt(i);

				slash = false;
			} else {
				output += input.charAt(i);
				slash = false;
			}
			
			i++;
		}
		
		return output;
	}
	
	public static String unescape(String input) {
		return unescape(input, '"', '\'', '\\');
	}
}
