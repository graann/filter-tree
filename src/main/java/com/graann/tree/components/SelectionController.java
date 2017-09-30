package com.graann.tree.components;

import javax.swing.*;
import javax.swing.tree.*;
import java.util.Collections;
import java.util.List;

public class SelectionController {
	private JTree tree;
	private TreeSelectionModel selectionModel;
	private List<DefaultMutableTreeNode> suitables = Collections.emptyList();

	SelectionController(JTree tree) {
		this.tree = tree;
		selectionModel = tree.getSelectionModel();
		selectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	}

	void setSuitables(List<DefaultMutableTreeNode> suitables) {
		this.suitables = suitables;
		if (suitables != null && !suitables.isEmpty()) {
			firstSuitable();
		}
	}

	void nextSuitable() {
		if (suitables == null || suitables.isEmpty()) return;

		DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) getSelectedNode();
		if (selectedNode == null) {
			firstSuitable();
			return;
		}

		int i = selectionModel.getLeadSelectionRow();
		boolean succeed;
		if(selectedNode.isLeaf() || tree.isExpanded(i)) {
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
		return node != null && node != getSelectedNode() && suitables.contains(node);
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
			return false;
		}

		return next(++i);
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

	private void firstSuitable() {
		TreePath path = getPath(suitables.get(0));
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
