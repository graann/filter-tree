package com.graann.tree.components;

import com.graann.tree.model.TreeModelControllerFactory;
import rx.subjects.BehaviorSubject;

public class TreeWidgetFactory {
	private TreeModelControllerFactory treeModelControllerFactory = new TreeModelControllerFactory();

	public TreeWidget create(BehaviorSubject<String> patternObservable) {
		TreeWidget widget = new TreeWidget();
		widget.setModelControllerFactory(treeModelControllerFactory);
		widget.setPatternObservable(patternObservable);
		widget.initialize();
		return widget;
	}

}
