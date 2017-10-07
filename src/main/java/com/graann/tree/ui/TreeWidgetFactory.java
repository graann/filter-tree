package com.graann.tree.ui;

class TreeWidgetFactory {
	private CustomTreeFactory treeFactory;

	void setTreeFactory(CustomTreeFactory treeFactory) {
		this.treeFactory = treeFactory;
	}

	public FilterTreeWidget create() {
		FilterTreeWidget widget = new FilterTreeWidget();
		widget.setCustomTreeFactory(treeFactory);
		widget.initialize();
		return widget;
	}

}
