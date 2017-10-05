package com.graann.tree.components;

import com.graann.treeloader.DictionaryLoader;
import com.graann.treeloader.TreeLoader;

/**
 * @author gromova on 20.09.17.
 */
public class DefaultTreePaneWidgetFactory implements TreePaneWidgetFactory {
	private final TreeLoader loader = new DictionaryLoader();
	private final TreeWidgetFactory treeWidgetFactory = new TreeWidgetFactory();

	public TreePaneWidget create() {
		TreePaneWidget tree = new TreePaneWidget();
		tree.setLoader(loader);
		tree.setTreeWidgetFactory(treeWidgetFactory);
		tree.initialize();
		return tree;
	}
}
