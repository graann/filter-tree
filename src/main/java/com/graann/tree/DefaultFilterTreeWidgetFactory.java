package com.graann.tree;

import com.graann.filter.FilterFactory;
import com.graann.filter.TrigramFilterFactory;
import com.graann.tree.components.TreeWidgetFactory;
import com.graann.treeloader.DefaultTreeLoader;
import com.graann.treeloader.TreeLoader;

/**
 * @author gromova on 20.09.17.
 */
public class DefaultFilterTreeWidgetFactory implements FilterTreeWidgetFactory {
	private TreeLoader loader = new DefaultTreeLoader();
	private FilterFactory filterFactory = new TrigramFilterFactory();
	private TreeWidgetFactory treeWidgetFactory = new TreeWidgetFactory();

	public FilterTreeWidget create() {
		FilterTreeWidget tree = new FilterTreeWidget();
		tree.setFilterFactory(filterFactory);
		tree.setLoader(loader);
		tree.setTreeWidgetFactory(treeWidgetFactory);
		tree.initialize();
		return tree;
	}
}
