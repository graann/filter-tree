package com.graann.tree.filter;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.Collections;
import java.util.List;

public class FilteredState {
	private final List<DefaultMutableTreeNode> filteredNodes;
	private final String pattern;
	private final TreeNode node;
	private final int total;
	private final int filtered;

	FilteredState(String pattern, TreeNode node,
				  List<DefaultMutableTreeNode> filteredNodes,
				  int total, int filtered) {
		this.filteredNodes = filteredNodes;
		this.pattern = pattern;
		this.node = node;
		this.total = total;
		this.filtered = filtered;
	}

	FilteredState(TreeNode node, int total) {
		this.node = node;
		this.total = total;
		this.filtered = total;
		filteredNodes = Collections.emptyList();
		pattern = "";
	}

	FilteredState(String pattern, int total) {
		this.node = null;
		this.total = total;
		this.filtered = 0;
		filteredNodes = Collections.emptyList();
		this.pattern = pattern;
	}

	public List<DefaultMutableTreeNode> getFilteredNodes() {
		return filteredNodes;
	}

	public String getPattern() {
		return pattern;
	}

	public TreeNode getRoot() {
		return node;
	}

	public int getTotal() {
		return total;
	}

	public int getFiltered() {
		return filtered;
	}
}
