package com.graann.tree;

import com.graann.filter.FilterFactory;
import com.graann.filter.TrigramFilterFactory;
import com.graann.treeloader.DefaultTreeLoader;
import com.graann.treeloader.TreeLoader;

/**
 * @author gromova on 20.09.17.
 */
public class DefaultFilterTreeWidgetFactory implements FilterTreeWidgetFactory {
	private TreeLoader loader = new DefaultTreeLoader();
	private FilterFactory filterFactory = new TrigramFilterFactory();

	public FilterTreeWidget create() {
		FilterTreeWidget tree = new FilterTreeWidget();
		tree.setFilterFactory(filterFactory);
		tree.setLoader(loader);
		tree.initialize();
		return tree;
	}
}
