package com.graann.tree;

/**
 * @author gromova on 20.09.17.
 */
public class DefaultFilterTreeWidgetFactory implements FilterTreeWidgetFactory {

	public FilterTreeWidget create() {
		FilterTreeWidget tree = new FilterTreeWidget();
		tree.initialize();
		return tree;
	}
}
