package com.graann.tree.model;

import rx.Observable;

public class TreeModelControllerFactory {
	private TreeFilter treeFilter = new DefaultTreeFilter();
	private StringFilterFactory stringFilterFactory = new StringFilterFactory();

	public TreeModelController create(Observable<String> patternObservable) {
		TreeModelController controller = new TreeModelController();
		controller.setTreeFilter(treeFilter);
		controller.setStringFilterFactory(stringFilterFactory);
		controller.setPatternObservable(patternObservable);
		return controller;
	}
}
