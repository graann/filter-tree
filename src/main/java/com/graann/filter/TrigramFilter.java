package com.graann.filter;

import com.graann.common.Utils;
import com.graann.treeloader.TreeStructure;
import org.apache.commons.collections4.trie.PatriciaTrie;
import rx.Observable;
import rx.schedulers.Schedulers;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TrigramFilter implements Filter {
	private final static int N = 3;
	private final static String SELECTION_STYLE = "<html>{0}<font color='yellow'>{1}</font>{2}</html>";
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

									Set<String> filtered = libraryTrie.prefixMap(gramKey)
											.values()
											.stream()
											.flatMap(Collection::stream)
											.distinct()
											.filter(s -> s.contains(pattern))
											.collect(Collectors.toSet());

									Set<TreeNode> filteredNodes = filtered
											.stream()
											.map(key -> treeStructure.getTreemap().get(key))
											.collect(Collectors.toSet());

									if (filteredNodes.isEmpty()) {
										return null;
									}
									Set<TreeNode> available = addParents(filteredNodes);
									Predicate<TreeNode> predicate = available::contains;

									Function<TreeNode, DefaultMutableTreeNode> creator = source -> {
										String s = source.toString();
/*										if (filtered.contains(s)) {
											String res = "<html>"+s.replace(pattern,"<font color='red'>"+pattern+"</font>")+"</html>";
											return new DefaultMutableTreeNode(res);
										}*/

										return new DefaultMutableTreeNode(s);
									};

									DefaultMutableTreeNode node = creator.apply(treeStructure.getRoot());
									addChildren(treeStructure.getRoot(), node, predicate, creator);
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


	private static void add(Map<String, Set<String>> map, String key, String value) {
		Set<String> set = map.computeIfAbsent(key, k -> new LinkedHashSet<>());
		set.add(value);
	}

	private static Set<TreeNode> addParents(Set<TreeNode> nods) {
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

	private static void addChildren(TreeNode source, DefaultMutableTreeNode destination, Predicate<TreeNode> predicate, Function<TreeNode, DefaultMutableTreeNode> creator) {
		Enumeration children = source.children();
		while (children.hasMoreElements()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) children.nextElement();
			if (predicate.test(node)) {
				DefaultMutableTreeNode newNode = creator.apply(node);
				destination.add(newNode);
				addChildren(node, newNode, predicate, creator);
			}
		}
	}
}
