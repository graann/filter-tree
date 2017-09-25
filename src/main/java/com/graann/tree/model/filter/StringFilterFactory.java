package com.graann.tree.model.filter;

import java.util.Set;

/**
 * @author gromova on 25.09.17.
 */
public class StringFilterFactory {
	public StringFilter create(Set<String> strings) {
		TrigramStringFilter trigramStringFilter = new TrigramStringFilter();
		trigramStringFilter.setStrings(strings);
		return trigramStringFilter;
	}
}
