package com.graann.tree.ui;

import com.graann.tree.model.TreeFilterFactory;

class TreeWidgetFactory {
	private final TreeFilterFactory treeFilterFactory = new TreeFilterFactory();

	public FilterTreeWidget create() {
		FilterTreeWidget widget = new FilterTreeWidget();
		widget.setTreeFilterFactory(treeFilterFactory);
		widget.initialize();
		return widget;
	}

}
