package com.graann.filter;

import com.graann.treeloader.TreeStructure;
import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.trie.PatriciaTrie;
import rx.Observable;
import rx.schedulers.Schedulers;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.function.Predicate;

public class Filtrator {
	private DefaultTreeModel treeModel;
	private TreeStructure treeStructure;

	private Trie<String, TreeNode> librabyTrie;
	private Observable<String> patternObservable;

	public void setPatternObservable(Observable<String> patternObservable) {
		this.patternObservable = patternObservable;
	}

	public void setTreeStructure(TreeStructure treeStructure) {
		this.treeStructure = treeStructure;
	}

	public void initialize() {
		treeModel = new DefaultTreeModel(treeStructure.getRoot());
		librabyTrie = new PatriciaTrie<>(treeStructure.getTreemap());
		patternObservable
				.observeOn(Schedulers.computation())
				.map((String s) -> {
					if(s == null || s.isEmpty()) {
						return treeStructure.getRoot();
					}

					SortedMap<String, TreeNode> stringTreeNodeSortedMap = librabyTrie.prefixMap(s);
					Set<TreeNode> available = new HashSet<>();

					for (TreeNode treeNode : stringTreeNodeSortedMap.values()) {
						if (treeNode.isLeaf()) {
							available.add(treeNode);
							TreeNode parent = treeNode.getParent();
							while (parent != null) {
								available.add(parent);
								parent = parent.getParent();
							}
						}
						available.add(treeNode);
					}

					if (available.isEmpty()) {
						return null;
					}

					Predicate<TreeNode> predicat = new Predicate<TreeNode>() {
						@Override
						public boolean test(TreeNode treeNode) {
							return available.contains(treeNode);
						}
					};

					DefaultMutableTreeNode node = new DefaultMutableTreeNode(((DefaultMutableTreeNode) treeStructure.getRoot()).getUserObject());
					addChildren(treeStructure.getRoot(), node, predicat);
					return node;
				})
				.subscribeOn(Schedulers.from(SwingUtilities::invokeLater))
				.subscribe(root -> treeModel.setRoot(root));

	}

	public TreeModel getModel() {
		return treeModel;

	}

	private void addChildren(TreeNode source, DefaultMutableTreeNode destination, Predicate<TreeNode> predicate) {
		Enumeration children = source.children();
		while (children.hasMoreElements()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) children.nextElement();
			if (predicate.test(node)) {
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(node.getUserObject());
				destination.add(newNode);
				addChildren(node, newNode, predicate);
			}
		}
	}
}
