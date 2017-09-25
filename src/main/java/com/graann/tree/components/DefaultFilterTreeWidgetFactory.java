package com.graann.tree.components;

import com.graann.treeloader.DefaultTreeLoader;
import com.graann.treeloader.TreeLoader;

/**
 * @author gromova on 20.09.17.
 */
public class DefaultFilterTreeWidgetFactory implements FilterTreeWidgetFactory {
	private TreeLoader loader = new DefaultTreeLoader();
	private TreeWidgetFactory treeWidgetFactory = new TreeWidgetFactory();

	public FilterTreeWidget create() {
		FilterTreeWidget tree = new FilterTreeWidget();
		tree.setLoader(loader);
		tree.setTreeWidgetFactory(treeWidgetFactory);
		tree.initialize();
		return tree;
	}
}