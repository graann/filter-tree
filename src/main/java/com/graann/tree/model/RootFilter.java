package com.graann.tree.model;

import com.graann.common.Reference;
import com.graann.treeloader.TreeStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.schedulers.Schedulers;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RootFilter implements TreeNodeFilter {
	private static final Logger LOG = LoggerFactory.getLogger(RootFilter.class);

	public Observable<TreeNode> rootObservable(TreeStructure treeStructure, Set<String> filtered) {
		return Observable.<TreeNode>create(subscriber -> {
			Set<TreeNode> filteredNodes = filtered
					.stream()
					.map(key -> treeStructure.getTreemap().get(key))
					.flatMap(Collection::stream)
					.parallel()
					.collect(Collectors.toSet());

			if (filteredNodes.isEmpty()) {
				LOG.debug("filteredNodes.isEmpty() return null");
				subscriber.onNext(null);
				return;
			}

			Set<TreeNode> available = addParents(filteredNodes);
			Predicate<TreeNode> predicate = available::contains;

			final List<DefaultMutableTreeNode> customTreeNodes = new ArrayList<>();
			final Reference<Integer> counter = new Reference<>(0);

			BiConsumer<TreeNode, DefaultMutableTreeNode> consumer = (source, node) -> {
				counter.setValue(counter.getValue()+1);
				if (filteredNodes.contains(source)) {
					customTreeNodes.add(node);
				}
			};

			RootTreeNode node = new RootTreeNode(treeStructure.getRoot().toString());
			node.setSelectedNodes(customTreeNodes);
			node.setFilteredCount(counter);

			consumer.accept(treeStructure.getRoot(), node);

			addChildren(treeStructure.getRoot(), node, predicate, consumer);
			subscriber.onNext(node);
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

		Enumeration children = source.children();
		while (children.hasMoreElements() && !Thread.currentThread().isInterrupted()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) children.nextElement();
			if (predicate.test(node)) {
				DefaultMutableTreeNode newNode = createNode(node);
				consumer.accept(node, newNode);

				destination.add(newNode);
				addChildren(node, newNode, predicate, consumer);
			}
		}
	}

	private static DefaultMutableTreeNode createNode(TreeNode source) {
		return new DefaultMutableTreeNode(source.toString());
	}
}
