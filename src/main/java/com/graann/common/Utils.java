package com.graann.common;

import java.util.HashSet;
import java.util.Set;

public class Utils {
	public static Set<String> getNGrams(String text, int n) {
		char[] chars = text.toCharArray();
		Set<String> set = new HashSet<>();
		for (int i = 0; i <= chars.length - n; i++) {
			StringBuilder sb = new StringBuilder();
			for (int j = 0; j < n; j++) {
				sb.append(chars[i+j]);
			}
			set.add(sb.toString());
		}
		return set;
	}

	/*	private Set<String> getEdge(char[] chars, int n) {
		Set<String> set = new HashSet<>();
		int i = 1;
		StringBuilder empty = new StringBuilder();
		while (i < n) {
			empty.append(" ");
			StringBuilder start = new StringBuilder(empty.toString());
			StringBuilder end = new StringBuilder();
			for (int j = 0; j < i; j++) {
				start.append(chars[j]);
				end.append(chars[chars.length - j - 1]);
			}

			end.append(empty.toString());

			set.add(start.toString());
			set.add(end.toString());
		}
	}*/
}
