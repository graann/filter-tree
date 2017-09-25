package com.graann.tree.model.filter;

import rx.Observable;

import java.util.Set;

/**
 * @author gromova on 25.09.17.
 */
public interface StringFilter {
	Observable<Set<String>> appropriateStringObservable(String pattern);
}
