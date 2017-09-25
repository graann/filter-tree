package com.graann.tree;

import com.graann.tree.components.TreeWidgetFactory;
import com.graann.tree.model.filter.DefaultTreeFilter;
import com.graann.tree.model.filter.TreeFilter;
import com.graann.treeloader.DefaultTreeLoader;
import com.graann.treeloader.TreeLoader;

/**
 * @author gromova on 20.09.17.
 */
public class DefaultFilterTreeWidgetFactory implements FilterTreeWidgetFactory {
	private TreeLoader loader = new DefaultTreeLoader();
	private TreeFilter treeFilter = new DefaultTreeFilter();
	private TreeWidgetFactory treeWidgetFactory = new TreeWidgetFactory();

	public FilterTreeWidget create() {
		FilterTreeWidget tree = new FilterTreeWidget();
		tree.setTreeFilter(treeFilter);
		tree.setLoader(loader);
		tree.setTreeWidgetFactory(treeWidgetFactory);
		tree.initialize();
		return tree;
	}
}
