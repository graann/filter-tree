package com.graann.tree;

import com.graann.tree.model.FilterTreeModelWrapper;
import com.graann.treeloader.DefaultTreeLoader;
import com.graann.treeloader.TreeLoader;

/**
 * @author gromova on 20.09.17.
 */
public class DefaultFilterTreeWidgetFactory implements FilterTreeWidgetFactory {
	private FilterTreeModelWrapper modelWrapper = new FilterTreeModelWrapper();
	private TreeLoader loader = new DefaultTreeLoader();

	public FilterTreeWidget create() {
		FilterTreeWidget tree = new FilterTreeWidget();
		tree.setModelWrapper(modelWrapper);
		tree.setLoader(loader);
		tree.initialize();
		return tree;
	}
}
