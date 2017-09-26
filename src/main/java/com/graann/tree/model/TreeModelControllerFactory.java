package com.graann.tree.model;

import rx.Observable;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;

public class TreeModelControllerFactory {
	private TreeFilter treeFilter = new DefaultTreeFilter();
	private StringFilterFactory stringFilterFactory = new StringFilterFactory();

	public TreeModelController create(DefaultTreeModel model, Observable<String> patternObservable, JTree tree) {
		TreeModelController controller = new TreeModelController();
		controller.setTreeFilter(treeFilter);
		controller.setStringFilterFactory(stringFilterFactory);
		controller.setPatternObservable(patternObservable);
		controller.setModel(model);
		controller.setTree(tree);
		return controller;
	}
}
