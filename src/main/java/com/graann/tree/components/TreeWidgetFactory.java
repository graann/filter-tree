package com.graann.tree.components;

import com.graann.tree.model.TreeFilterFactory;

public class TreeWidgetFactory {
	private TreeFilterFactory treeFilterFactory = new TreeFilterFactory();

	public TreeWidget create() {
		TreeWidget widget = new TreeWidget();
		widget.setModelControllerFactory(treeFilterFactory);
		widget.initialize();
		return widget;
	}

}
