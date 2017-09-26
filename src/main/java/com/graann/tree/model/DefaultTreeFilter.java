package com.graann.tree.model;

import com.graann.treeloader.TreeStructure;
import org.reactfx.util.Tuple2;
import org.reactfx.util.Tuples;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DefaultTreeFilter implements TreeFilter {
	private static final Logger LOG = LoggerFactory.getLogger(DefaultTreeFilter.class);

	public Observable<Tuple2<DefaultMutableTreeNode, List<DefaultMutableTreeNode>>> rootObservable(TreeStructure treeStructure, Set<String> filtered) {
		return Observable.<Tuple2<DefaultMutableTreeNode, List<DefaultMutableTreeNode>>>create(subscriber -> {
			LOG.debug("rootObservable");
			Set<TreeNode> filteredNodes = filtered
					.stream()
					.map(key -> treeStructure.getTreemap().get(key))
					.flatMap(Collection::stream)
					.collect(Collectors.toSet());

			if (filteredNodes.isEmpty()) {
				LOG.debug("filteredNodes.isEmpty() return null");
				subscriber.onNext(null);
				return;
			}

			AtomicBoolean process = new AtomicBoolean(true);

			Set<TreeNode> available = addParents(process, filteredNodes);
			if (!process.get()) {
				LOG.debug("process false");
				return;
			}

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
			addChildren(process, treeStructure.getRoot(), node, predicate, creator);

			if (!process.get()) {
				LOG.debug("process false");
				return;
			}

			LOG.debug("onNext customTreeNodes.size: ");
			subscriber.onNext(Tuples.t(node, customTreeNodes));

			subscriber.add(Subscriptions.create(() -> process.set(false)));
		}).subscribeOn(Schedulers.computation());
	}

	private static Set<TreeNode> addParents(AtomicBoolean process, Set<TreeNode> nods) {
		Set<TreeNode> available = new LinkedHashSet<>();
		if (!process.get()) {
			return available;
		}

		for (TreeNode treeNode : nods) {
			TreeNode parent = treeNode.getParent();
			if (!available.contains(parent)) {
				while (parent != null && process.get()) {
					available.add(parent);
					parent = parent.getParent();
				}
			}

			available.add(treeNode);
		}
		return available;
	}

	private static void addChildren(AtomicBoolean process, TreeNode source, DefaultMutableTreeNode destination,
									Predicate<TreeNode> predicate, Function<TreeNode, DefaultMutableTreeNode> creator) {
		if (!process.get()) {
			return;
		}
		Enumeration children = source.children();
		while (children.hasMoreElements()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) children.nextElement();
			if (predicate.test(node)) {
				DefaultMutableTreeNode newNode = creator.apply(node);
				destination.add(newNode);
				addChildren(process, node, newNode, predicate, creator);
			}
		}
	}
}
