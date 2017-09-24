package com.graann.common;

import java.util.HashSet;
import java.util.Set;

public class Utils {
	public static Set<String> getNGrams(String text, int n) {
		char[] chars = text.toCharArray();
		Set<String> set = new HashSet<>();
		for (int i = 0; i < chars.length; i++) {
			StringBuilder sb = new StringBuilder();
			for (int j = 0; j < n && i + j < chars.length; j++) {
				sb.append(chars[i + j]);
			}
			set.add(sb.toString());
		}
		return set;
	}
}
