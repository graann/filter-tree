package com.graann.tree.filter;

import com.graann.treeloader.TreeStructure;
import rx.Observable;

public class TreeFilterFactory {
	private final TreeNodeFilter treeNodeFilter = new RootFilter();
	private final StringFilterFactory stringFilterFactory = new StringFilterFactory();
	private Observable<TreeStructure> structureObservable;

	public void setStructureObservable(Observable<TreeStructure> structureObservable) {
		this.structureObservable = structureObservable;
	}

	public TreeFilter create(Observable<String> patternObservable) {
		TreeFilter controller = new TreeFilter();
		controller.setStructureObservable(structureObservable);
		controller.setTreeNodeFilter(treeNodeFilter);
		controller.setStringFilterFactory(stringFilterFactory);
		controller.setPatternObservable(patternObservable);
		controller.init();
		return controller;
	}
}
