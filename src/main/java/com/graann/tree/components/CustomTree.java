package com.graann.tree.components;

import javax.swing.JTree;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

/**
 * @author gromova on 22.09.17.
 */
public class CustomTree extends JTree {
	private boolean stopFiering = false;

	@Override
	public void fireTreeExpanded(TreePath path) {
		super.fireTreeExpanded(path);
	}

	@Override
	public void fireTreeCollapsed(TreePath path) {
		super.fireTreeCollapsed(path);
	}

	@Override
	public void fireTreeWillExpand(TreePath path) throws ExpandVetoException {
		super.fireTreeWillExpand(path);
	}

	@Override
	public void fireTreeWillCollapse(TreePath path) throws ExpandVetoException {
		super.fireTreeWillCollapse(path);
	}
}
