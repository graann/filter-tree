package com.graann.tree.model;

import com.graann.treeloader.TreeStructure;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import rx.Scheduler;
import rx.schedulers.Schedulers;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class RootFilterTest {
	private final TreeNodeFilter treeNodeFilter = new RootFilter();
	private TreeStructure treeStructure;

	private Map<String, Set<TreeNode>> map = new LinkedHashMap<>();

	private DefaultMutableTreeNode root = createNode("abc", map);
	private DefaultMutableTreeNode ch0 = createNode("filter tree", map);
	private DefaultMutableTreeNode ch00 = createNode("import", map);
	private DefaultMutableTreeNode ch10 = createNode("import", map);
	private DefaultMutableTreeNode ch1 = createNode("stop", map);
	private DefaultMutableTreeNode ch11 = createNode("action", map);
	private DefaultMutableTreeNode ch12= createNode("widget", map);
	private DefaultMutableTreeNode ch120 = createNode("tree", map);

	@Before
	public void setUp() throws Exception {
		root.add(ch0);
		root.add(ch1);
		ch0.add(ch00);
		ch1.add(ch10);
		ch1.add(ch11);
		ch1.add(ch12);
		ch12.add(ch120);

		treeStructure = new TreeStructure(root, map, 6);
	}

	private static DefaultMutableTreeNode createNode(String string, Map<String, Set<TreeNode>> map) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(string);

		Set<TreeNode> treeNodes = map.computeIfAbsent(string, k -> new HashSet<>());
		treeNodes.add(node);
		return node;
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void rootObservable() throws Exception {
		Set<String> set = new HashSet<>();
		set.add(ch00.toString());
		set.add(ch0.toString());
		set.add(ch120.toString());

		treeNodeFilter.rootObservable(treeStructure, set)
				.subscribe(treeNode -> {
					assertNotNull(treeNode);
					assertTrue(treeNode instanceof RootTreeNode);
					RootTreeNode root = (RootTreeNode) treeNode;

					assertEquals(root.toString(), root.toString());

					TreeNode child0 = root.getChildAt(0);
					assertEquals(child0.toString(), ch0.toString());

					TreeNode child01 = child0.getChildAt(0);
					assertEquals(child01.toString(), ch00.toString());

					TreeNode child1 = root.getChildAt(1);
					assertEquals(child1.toString(), ch1.toString());

					TreeNode child10 = child1.getChildAt(0);
					assertEquals(child10.toString(), ch10.toString());

					TreeNode child11 = child1.getChildAt(1);
					assertEquals(child11.toString(), ch12.toString());

					TreeNode child110 = child11.getChildAt(0);
					assertEquals(child110.toString(), ch120.toString());
				});

		Thread.sleep(500);
	}

	@Test
	public void emptySetObservable() throws Exception {
		treeNodeFilter.rootObservable(treeStructure, Collections.emptySet())
				.subscribe(Assert::assertNull);

		Thread.sleep(500);
	}

}