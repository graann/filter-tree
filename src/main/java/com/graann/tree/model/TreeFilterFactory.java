package com.graann.tree.model;

import rx.Observable;

public class TreeFilterFactory {
	private TreeNodeFilter treeNodeFilter = new DefaultTreeNodeFilter();
	private StringFilterFactory stringFilterFactory = new StringFilterFactory();

	public TreeFilter create(Observable<String> patternObservable) {
		TreeFilter controller = new TreeFilter();
		controller.setTreeNodeFilter(treeNodeFilter);
		controller.setStringFilterFactory(stringFilterFactory);
		controller.setPatternObservable(patternObservable);
		return controller;
	}
}
