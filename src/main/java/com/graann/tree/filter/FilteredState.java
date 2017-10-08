package com.graann.tree.filter;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.Collections;
import java.util.Set;

public class FilteredState {
	private final Set<DefaultMutableTreeNode> filteredNodes;
	private final String pattern;
	private final TreeNode node;
	private final int total;
	private final int filtered;

	FilteredState(String pattern, TreeNode node,
				  Set<DefaultMutableTreeNode> filteredNodes,
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
		filteredNodes = Collections.emptySet();
		pattern = "";
	}

	FilteredState(String pattern, int total) {
		this.node = null;
		this.total = total;
		this.filtered = 0;
		filteredNodes = Collections.emptySet();
		this.pattern = pattern;
	}

	public Set<DefaultMutableTreeNode> getFilteredNodes() {
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
