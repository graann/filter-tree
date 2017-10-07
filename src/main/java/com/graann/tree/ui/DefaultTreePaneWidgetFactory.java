package com.graann.tree.ui;

import com.graann.tree.filter.TreeFilterFactory;
import com.graann.treeloader.TxtLoader;
import com.graann.treeloader.TreeLoader;

/**
 * @author gromova on 20.09.17.
 */
public class DefaultTreePaneWidgetFactory implements TreePaneWidgetFactory {
	private final TreeLoader loader = new TxtLoader();

	public TreePaneWidget create(String fileName) {
		TreeFilterFactory treeFilterFactory = new TreeFilterFactory();
		treeFilterFactory.setStructureObservable(loader.loadTreeStructure(fileName));

		CustomTreeFactory treeFactory = new CustomTreeFactory();
		treeFactory.setTreeFilterFactory(treeFilterFactory);

		TreeWidgetFactory treeWidgetFactory = new TreeWidgetFactory();
		treeWidgetFactory.setTreeFactory(treeFactory);

		TreePaneWidget tree = new TreePaneWidget();
		tree.setTreeWidgetFactory(treeWidgetFactory);
		tree.initialize();
		return tree;
	}
}
