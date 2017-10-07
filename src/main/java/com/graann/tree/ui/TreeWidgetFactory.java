package com.graann.tree.ui;

import com.graann.tree.filter.TreeFilterFactory;
import com.graann.treeloader.TreeStructure;
import rx.Observable;

class TreeWidgetFactory {
	public FilterTreeWidget create(Observable<TreeStructure> structureObservable) {
		TreeFilterFactory treeFilterFactory = new TreeFilterFactory();
		treeFilterFactory.setStructureObservable(structureObservable);

		CustomTreeFactory treeFactory = new CustomTreeFactory();
		treeFactory.setTreeFilterFactory(treeFilterFactory);

		FilterTreeWidget widget = new FilterTreeWidget();
		widget.setCustomTreeFactory(treeFactory);
		widget.initialize();
		return widget;
	}

}
