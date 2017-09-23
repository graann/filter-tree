package com.graann.filter;

import com.graann.common.Utils;
import com.graann.treeloader.TreeStructure;
import org.apache.commons.collections4.trie.PatriciaTrie;
import rx.Observable;
import rx.schedulers.Schedulers;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import java.util.*;
import java.util.function.Predicate;

public class Filtrator {
	private DefaultTreeModel treeModel;
	private TreeStructure treeStructure;

	private Observable<String> patternObservable;

	public void setPatternObservable(Observable<String> patternObservable) {
		this.patternObservable = patternObservable;
	}

	public void setTreeStructure(TreeStructure treeStructure) {
		this.treeStructure = treeStructure;
	}

	public void initialize() {
		treeModel = new DefaultTreeModel(treeStructure.getRoot());
		createTrieObservable()
				.observeOn(Schedulers.computation())
				.subscribeOn(Schedulers.from(SwingUtilities::invokeLater))
				.first()
				.switchMap(librabyTrie ->
						patternObservable
								.map((String s) -> {
									if (s == null || s.isEmpty()) {
										return treeStructure.getRoot();
									}
									SortedMap<String, TreeNode> stringTreeNodeSortedMap = librabyTrie.prefixMap(s);
									Set<TreeNode> available = getAvailable(stringTreeNodeSortedMap);

									if (available.isEmpty()) {
										return null;
									}

									Predicate<TreeNode> predicat = available::contains;

									DefaultMutableTreeNode node = new DefaultMutableTreeNode(((DefaultMutableTreeNode) treeStructure.getRoot()).getUserObject());
									addChildren(treeStructure.getRoot(), node, predicat);
									return node;
								})
				)
				.subscribe(root -> treeModel.setRoot(root));
	}

	private Observable<PatriciaTrie<TreeNode>> createTrieObservable() {
		return Observable.create(subscriber -> {
			Map<String, TreeNode> map = new LinkedHashMap<>();
			treeStructure.getTreemap().forEach((s, treeNode) -> {
				map.put(s, treeNode);
				Set<String> nGrams = Utils.getNGrams(s, 3);
				for (String nGram : nGrams) {
					map.put(nGram, treeNode);
				}
			});
			subscriber.onNext(new PatriciaTrie<>(map));
		});
	}

	private Set<TreeNode> getAvailable(SortedMap<String, TreeNode> filteredMap) {
		HashSet<TreeNode> available = new HashSet<>();

		for (TreeNode treeNode : filteredMap.values()) {
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
		return available;
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