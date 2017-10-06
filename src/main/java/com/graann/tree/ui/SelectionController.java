package com.graann.tree.ui;

import javax.swing.*;
import javax.swing.tree.*;
import java.util.Collections;
import java.util.List;

class SelectionController {
	private final JTree tree;
	private final TreeSelectionModel selectionModel;
	private List<DefaultMutableTreeNode> suitable = Collections.emptyList();

	SelectionController(JTree tree) {
		this.tree = tree;
		tree.setExpandsSelectedPaths(true);
		selectionModel = tree.getSelectionModel();
		selectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	}

	void setSuitable(List<DefaultMutableTreeNode> suitable) {
		this.suitable = suitable;
		if (suitable != null && !suitable.isEmpty()) {
			firstSuitable();
		}
	}

	void previousSuitable() {
		if (suitable == null || suitable.isEmpty()) return;
		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) getSelectedNode();
		if (selectedNode == null) {
			firstSuitable();
			return;
		}

		int i = selectionModel.getLeadSelectionRow();
		boolean succeed;
		succeed = previous(--i);

		if (!succeed) {
			firstSuitable();
		}
	}

	void nextSuitable() {
		if (suitable == null || suitable.isEmpty()) return;

		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) getSelectedNode();
		if (selectedNode == null) {
			firstSuitable();
			return;
		}

		int i = selectionModel.getLeadSelectionRow();
		boolean succeed;
		if (selectedNode.isLeaf() || tree.isExpanded(i)) {
			succeed = next(++i);
		} else {
			succeed = firstSuitableChild(selectedNode);
		}

		if (!succeed) {
			firstSuitable();
		}
	}

	private TreeNode getSelectedNode() {
		if (selectionModel.isSelectionEmpty()) {
			return null;
		}

		int i = selectionModel.getLeadSelectionRow();
		return getNode(i);
	}

	private boolean check(DefaultMutableTreeNode node) {
		return node != null && node != getSelectedNode() && suitable.contains(node);
	}

	private boolean next(int i) {
		DefaultMutableTreeNode node = getNode(i);
		if (node == null) {
			return false;
		}
		if (check(node)) {
			setSelection(node);
			return true;
		}

		if (node.isLeaf()) {
			return next(++i);
		}

		return firstSuitableChild(node);
	}

	private boolean previous(int i) {
		if (i < 0) {
			return false;
		}

		DefaultMutableTreeNode node = getNode(i);
		if (node == null) {
			return false;
		}

		if (tree.isExpanded(i) || node.isLeaf()) {
			if (check(node)) {
				setSelection(node);
				return true;
			}
			return previous(--i);
		}

		return lastSuitableChild(node);
	}

	private boolean firstSuitableChild(DefaultMutableTreeNode node) {
		while (!node.isLeaf()) {
			node = (DefaultMutableTreeNode) node.getFirstChild();
			if (check(node)) {
				setSelection(node);
				return true;
			}
		}
		return false;
	}

	private boolean lastSuitableChild(DefaultMutableTreeNode node) {
		while (!node.isLeaf()) {
			node = (DefaultMutableTreeNode) node.getLastChild();
		}
		if (check(node)) {
			setSelection(node);
			return true;
		}
		return false;
	}

	private void firstSuitable() {
		TreePath path = getPath(suitable.get(0));
		setSelectionPath(path);
	}

	private void setSelection(TreeNode node) {
		TreePath path = getPath(node);
		setSelectionPath(path);
	}

	private void setSelectionPath(TreePath path) {
		tree.setSelectionPath(path);
		tree.scrollPathToVisible(path);
	}

	private DefaultMutableTreeNode getNode(int row) {
		TreePath pathForRow = tree.getPathForRow(row);
		if (pathForRow == null) {
			return null;
		}

		TreeNode node = (TreeNode) pathForRow.getLastPathComponent();

		return (DefaultMutableTreeNode) node;
	}

	private TreePath getPath(TreeNode node) {
		DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
		TreeNode[] nodes = model.getPathToRoot(node);
		return new TreePath(nodes);
	}

}
