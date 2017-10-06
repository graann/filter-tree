package com.graann.tree.ui;

import com.graann.treeloader.TxtLoader;
import com.graann.treeloader.TreeLoader;

/**
 * @author gromova on 20.09.17.
 */
public class DefaultTreePaneWidgetFactory implements TreePaneWidgetFactory {
	private final TreeLoader loader = new TxtLoader();
	private final TreeWidgetFactory treeWidgetFactory = new TreeWidgetFactory();

	public TreePaneWidget create(String fileName) {
		TreePaneWidget tree = new TreePaneWidget();
		tree.setFileName(fileName);
		tree.setLoader(loader);
		tree.setTreeWidgetFactory(treeWidgetFactory);
		tree.initialize();
		return tree;
	}
}
