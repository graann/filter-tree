package com.graann.tree;

import com.graann.tree.model.FilterTreeModelWrapper;

/**
 * @author gromova on 20.09.17.
 */
public class DefaultFilterTreeWidgetFactory implements FilterTreeWidgetFactory {
	private FilterTreeModelWrapper modelWrapper = new FilterTreeModelWrapper();

	public FilterTreeWidget create() {
		FilterTreeWidget tree = new FilterTreeWidget();
		tree.setModelWrapper(modelWrapper);
		tree.initialize();
		return tree;
	}
}
