package com.graann.tree.components;

import com.graann.tree.model.TreeModelControllerFactory;

public class TreeWidgetFactory {
	private TreeModelControllerFactory treeModelControllerFactory = new TreeModelControllerFactory();

	public TreeWidget create() {
		TreeWidget widget = new TreeWidget();
		widget.setModelControllerFactory(treeModelControllerFactory);
		widget.initialize();
		return widget;
	}

}
