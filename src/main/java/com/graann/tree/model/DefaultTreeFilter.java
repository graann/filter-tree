package com.graann.tree.model;

import com.graann.treeloader.TreeStructure;
import org.reactfx.util.Tuple2;
import org.reactfx.util.Tuples;
import rx.Observable;
import rx.schedulers.Schedulers;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DefaultTreeFilter implements TreeFilter {
	private final static String SELECTION_STYLE = "<html>{0}<font color='yellow'>{1}</font>{2}</html>";

	public Observable<Tuple2<DefaultMutableTreeNode, List<DefaultMutableTreeNode>>> rootObservable(TreeStructure treeStructure, Set<String> filtered) {
		return Observable.<Tuple2<DefaultMutableTreeNode, List<DefaultMutableTreeNode>>>create(subscriber -> {
			Set<TreeNode> filteredNodes = filtered
					.stream()
					.map(key -> treeStructure.getTreemap().get(key))
					.flatMap(Collection::stream)
					.collect(Collectors.toSet());

			if (filteredNodes.isEmpty()) {
				subscriber.onNext(null);
				return;
			}
			Set<TreeNode> available = addParents(filteredNodes);
			Predicate<TreeNode> predicate = available::contains;

			final List<DefaultMutableTreeNode> customTreeNodes = new ArrayList<>();

			Function<TreeNode, DefaultMutableTreeNode> creator = source -> {
				String s = source.toString();
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(s);
				if (filtered.contains(s)) {
					customTreeNodes.add(node);
				}

				return node;
			};

			DefaultMutableTreeNode node = creator.apply(treeStructure.getRoot());
			addChildren(treeStructure.getRoot(), node, predicate, creator);

			subscriber.onNext(Tuples.t(node, customTreeNodes));
		}).subscribeOn(Schedulers.computation());
	}

	private static Set<TreeNode> addParents(Set<TreeNode> nods) {
		HashSet<TreeNode> available = new HashSet<>();

		for (TreeNode treeNode : nods) {
			TreeNode parent = treeNode.getParent();
			if (!available.contains(parent)) {
				while (parent != null) {
					available.add(parent);
					parent = parent.getParent();
				}
			}

			available.add(treeNode);
		}
		return available;
	}

	private static void addChildren(TreeNode source, DefaultMutableTreeNode destination,
									Predicate<TreeNode> predicate, Function<TreeNode, DefaultMutableTreeNode> creator) {
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
