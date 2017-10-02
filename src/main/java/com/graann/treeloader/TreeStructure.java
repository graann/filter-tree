package com.graann.treeloader;

import javax.swing.tree.TreeNode;
import java.util.Map;
import java.util.Set;

/**
 * @author gromova on 22.09.17.
 */
public final class TreeStructure {
	private final TreeNode root;
	private final Map<String, Set<TreeNode>> treemap;
	private final int count;

	public TreeNode getRoot() {
		return root;
	}

	public Map<String, Set<TreeNode>> getTreemap() {
		return treemap;
	}

	public Set<String> getStrings() {
		return treemap.keySet();
	}

	public int getCount() {
		return count;
	}

	TreeStructure(TreeNode root, Map<String, Set<TreeNode>> treemap, int count) {
		this.root = root;
		this.treemap = treemap;
		this.count = count;
	}
}
