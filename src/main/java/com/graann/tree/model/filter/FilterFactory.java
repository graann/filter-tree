package com.graann.tree.model.filter;

import rx.Observable;

/**
 * @author gromova on 22.09.17.
 */
public interface FilterFactory {
	Filter create(Observable<String> patternObservable);
}
