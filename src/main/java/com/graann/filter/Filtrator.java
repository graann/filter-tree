package com.graann.filter;

import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.trie.PatriciaTrie;
import rx.Observable;
import rx.schedulers.Schedulers;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import java.util.*;
import java.util.function.Predicate;

public class Filtrator {
	private TreeNode root;
	private Map<String, TreeNode> map;
	private Observable<String> patternObservable;

	public void setMap(Map<String, TreeNode> map) {
		this.map = map;
	}

	public void setRoot(TreeNode root) {
		this.root = root;
	}

	public void setPatternObservable(Observable<String> patternObservable) {
		this.patternObservable = patternObservable;
	}

	public Observable<TreeModel> getModel() {
		Trie<String, TreeNode> librabyTrie = new PatriciaTrie<>(map);
		return patternObservable
				.observeOn(Schedulers.computation())
				.map(s -> {
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
					}

					if (available.isEmpty()) {
						return new DefaultTreeModel(null);
					}

					DefaultMutableTreeNode node = new DefaultMutableTreeNode(((DefaultMutableTreeNode) root).getUserObject());
					addChildren(root, node, treeNode -> available.contains(treeNode));
					return new DefaultTreeModel(node);

				});

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
