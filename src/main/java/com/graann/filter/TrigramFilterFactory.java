package com.graann.filter;

import rx.Observable;

public class TrigramFilterFactory implements FilterFactory {

	@Override
	public Filter create(Observable<String> patternObservable) {
		TrigramFilter trigramFilter = new TrigramFilter();
		trigramFilter.setPatternObservable(patternObservable);
		return trigramFilter;
	}
}
