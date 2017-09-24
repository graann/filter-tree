package com.graann.filter;

import com.graann.common.Utils;
import com.graann.treeloader.TreeStructure;
import org.apache.commons.collections4.trie.PatriciaTrie;
import rx.Observable;
import rx.schedulers.Schedulers;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TrigramFilter implements Filter {
	private final static int N = 3;
	private Observable<String> patternObservable;

	public void setPatternObservable(Observable<String> patternObservable) {
		this.patternObservable = patternObservable;
	}

	@Override
	public Observable<TreeNode> rootObservable(TreeStructure treeStructure) {
		return createTrieObservable(treeStructure)
				.observeOn(Schedulers.computation())
				.first()
				.switchMap(libraryTrie ->
						patternObservable
								.map((String pattern) -> {
									if (pattern == null || pattern.isEmpty()) {
										return treeStructure.getRoot();
									}

									String gramKey = pattern.length() <= N ? pattern : pattern.substring(0, N);

									Set<TreeNode> filtered = libraryTrie.prefixMap(gramKey)
											.values()
											.stream()
											.flatMap(Collection::stream)
											.distinct()
											.filter(s -> s.contains(pattern))
											.map(key -> treeStructure.getTreemap().get(key))
											.collect(Collectors.toSet());

									if (filtered.isEmpty()) {
										return null;
									}
									Set<TreeNode> available = addParents(filtered);
									Predicate<TreeNode> predicate = available::contains;

									DefaultMutableTreeNode node = new DefaultMutableTreeNode(((DefaultMutableTreeNode) treeStructure.getRoot()).getUserObject());
									addChildren(treeStructure.getRoot(), node, predicate);
									return node;
								})
				);

	}

	private Observable<PatriciaTrie<Set<String>>> createTrieObservable(TreeStructure treeStructure) {
		return Observable.create(subscriber -> {
			Map<String, Set<String>> map = new LinkedHashMap<>();
			treeStructure.getTreemap().forEach((s, treeNode) -> {
				add(map, s, s);

				Set<String> nGrams = Utils.getNGrams(s, N);
				nGrams.forEach(nGram -> add(map, nGram, s));
			});
			subscriber.onNext(new PatriciaTrie<>(map));
		});
	}

	private void add(Map<String, Set<String>> map, String key, String value) {
		Set<String> set = map.computeIfAbsent(key, k -> new LinkedHashSet<>());
		set.add(value);
	}

	private Set<TreeNode> addParents(Set<TreeNode> nods) {
		HashSet<TreeNode> available = new HashSet<>();

		for (TreeNode treeNode : nods) {
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
