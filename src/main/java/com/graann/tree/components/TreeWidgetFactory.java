package com.graann.tree.components;

import com.graann.tree.model.TreeFilterFactory;

public class TreeWidgetFactory {
	private TreeFilterFactory treeFilterFactory = new TreeFilterFactory();

	public FilterTreeWidget create() {
		FilterTreeWidget widget = new FilterTreeWidget();
		widget.setTreeFilterFactory(treeFilterFactory);
		widget.initialize();
		return widget;
	}

}
