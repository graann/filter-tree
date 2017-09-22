package com.graann.treeloader;

import javax.swing.tree.TreeNode;
import java.util.Map;

/**
 * @author gromova on 22.09.17.
 */
public final class TreeStructure {
	private final TreeNode root;
	private final Map<String, TreeNode> treemap;

	public TreeNode getRoot() {
		return root;
	}

	public Map<String, TreeNode> getTreemap() {
		return treemap;
	}

	TreeStructure(TreeNode root, Map<String, TreeNode> treemap) {
		this.root = root;
		this.treemap = treemap;
	}
}
