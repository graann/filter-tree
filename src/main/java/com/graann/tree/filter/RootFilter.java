package com.graann.tree.filter;

import com.graann.common.Reference;
import com.graann.treeloader.TreeStructure;
import org.reactfx.util.Tuple3;
import org.reactfx.util.Tuples;
import rx.Observable;
import rx.schedulers.Schedulers;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RootFilter implements TreeNodeFilter {

	public Observable<Tuple3<TreeNode, Set<DefaultMutableTreeNode>, Integer>> rootObservable(TreeStructure treeStructure, Set<String> filtered) {
		return Observable.<Tuple3<TreeNode, Set<DefaultMutableTreeNode>, Integer>>create(subscriber -> {
			Set<TreeNode> filteredNodes = filtered
					.stream()
					.map(key -> treeStructure.getTreemap().get(key))
					.flatMap(Collection::stream)
					.parallel()
					.collect(Collectors.toSet());

			if (filteredNodes.isEmpty()) {
				subscriber.onNext(null);
				return;
			}

			Set<TreeNode> available = addParents(filteredNodes);
			Predicate<TreeNode> predicate = available::contains;

			final Set<DefaultMutableTreeNode> customTreeNodes = new LinkedHashSet<>();
			final Reference<Integer> counter = new Reference<>(0);

			BiConsumer<TreeNode, DefaultMutableTreeNode> consumer = (source, node) -> {
				counter.setValue(counter.getValue()+1);
				if (filteredNodes.contains(source)) {
					customTreeNodes.add(node);
				}
			};

			DefaultMutableTreeNode node = new DefaultMutableTreeNode(treeStructure.getRoot().toString());
			consumer.accept(treeStructure.getRoot(), node);

			addChildren(treeStructure.getRoot(), node, predicate, consumer);
			subscriber.onNext(Tuples.t(node, customTreeNodes, counter.getValue()));
		}).subscribeOn(Schedulers.computation());
	}

	private static Set<TreeNode> addParents(Set<TreeNode> nods) {
		Set<TreeNode> available = new LinkedHashSet<>();

		for (TreeNode treeNode : nods) {
			if (Thread.currentThread().isInterrupted()) {
				return available;
			}
			TreeNode parent = treeNode.getParent();
			if (!available.contains(parent)) {
				while (parent != null && !Thread.currentThread().isInterrupted()) {
					available.add(parent);
					parent = parent.getParent();
				}
			}

			available.add(treeNode);
		}
		return available;
	}

	private static void addChildren(TreeNode source, DefaultMutableTreeNode destination,
									Predicate<TreeNode> predicate, BiConsumer<TreeNode, DefaultMutableTreeNode> consumer) {
		if (Thread.currentThread().isInterrupted()) {
			return;
		}

		for (int i = 0; i < source.getChildCount() && !Thread.currentThread().isInterrupted(); i++) {
			TreeNode node = source.getChildAt(i);
			if (predicate.test(node)) {
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(node.toString());
				consumer.accept(node, newNode);

				destination.add(newNode);
				addChildren(node, newNode, predicate, consumer);
			}
		}
	}
}
