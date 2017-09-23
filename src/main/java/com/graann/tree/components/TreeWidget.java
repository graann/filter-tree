package com.graann.tree.components;

import com.graann.common.Viewable;

import javax.swing.*;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author gromova on 22.09.17.
 */
public class TreeWidget implements Viewable<JComponent> {
	private JTree tree;
	private JScrollPane scrollPane;

	private TreeModel model;

	public void setModel(TreeModel model) {
		this.model = model;
	}

	@Override
	public JComponent getView() {
		return scrollPane;
	}

	public void initialize() {
		tree = new JTree(model);
		tree.setExpandsSelectedPaths(true);
		scrollPane = new JScrollPane(tree);
	}

	@Override
	public void destroy() {

	}

	public void expandNodes() {
		expandVisible(tree);
	}

	private void expandVisible(JTree tree) {

		JViewport viewport = scrollPane.getViewport();
		Point point = viewport.getViewPosition();

		TreePath firstPath = tree.getClosestPathForLocation(point.x, point.y);
		int firstIndex = tree.getRowForPath(firstPath);

		TreePath lastPath = tree.getClosestPathForLocation(point.x, point.y + viewport.getWidth());
		int lastIndex = tree.getRowForPath(lastPath);

		expandNodes(tree, firstIndex, lastIndex);
	}

	private static void expandNodes(JTree tree, int startingIndex, int stopIndex) {
		for (int i = startingIndex; i <= stopIndex; i++) {
			tree.expandRow(i);
		}
	}

	/**
	 * TODO move to model
	 */

	public static TreePath getPath(TreeNode treeNode) {
		java.util.List<Object> nodes = new ArrayList<>();
		if (treeNode != null) {
			nodes.add(treeNode);
			treeNode = treeNode.getParent();
			while (treeNode != null) {
				nodes.add(0, treeNode);
				treeNode = treeNode.getParent();
			}
		}

		return nodes.isEmpty() ? null : new TreePath(nodes.toArray());
	}

	private static java.util.List<TreeNode> getAllFinalNode(TreeNode node) {
		List<TreeNode> leafNodes = new ArrayList<>();

		if (node.isLeaf()) {
			leafNodes.add(node.getParent());
		} else {
			Enumeration children = node.children();
			while (children.hasMoreElements()) {
				leafNodes.addAll(getAllFinalNode((TreeNode) children.nextElement()));
			}
		}
		return leafNodes;
	}

	/*	private boolean stopFiering = false;

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
	}*/
}
