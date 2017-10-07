package com.graann.tree.filter;

import com.graann.treeloader.TreeStructure;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.*;

import static org.junit.Assert.*;

public class RootFilterTest {
	private final TreeNodeFilter treeNodeFilter = new RootFilter();

	private final Map<String, Set<TreeNode>> map = new LinkedHashMap<>();

	private final DefaultMutableTreeNode root = createNode("abc", map);
	private final DefaultMutableTreeNode ch0 = createNode("filter tree", map);
	private final DefaultMutableTreeNode ch00 = createNode("import", map);
	private final DefaultMutableTreeNode ch10 = createNode("import", map);
	private final DefaultMutableTreeNode ch1 = createNode("stop", map);
	private final DefaultMutableTreeNode ch11 = createNode("action", map);
	private final DefaultMutableTreeNode ch12= createNode("widget", map);
	private final DefaultMutableTreeNode ch120 = createNode("tree", map);

	private final Object lock = new Object();
	private volatile boolean semaphore = false;
	private TreeStructure treeStructure;

	@Before
	public void setUp() throws Exception {
		root.add(ch0);
		root.add(ch1);
		ch0.add(ch00);
		ch1.add(ch10);
		ch1.add(ch11);
		ch1.add(ch12);
		ch12.add(ch120);

		treeStructure = new TreeStructure(root, map, 7);
	}

	private static DefaultMutableTreeNode createNode(String string, Map<String, Set<TreeNode>> map) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(string);

		Set<TreeNode> treeNodes = map.computeIfAbsent(string, k -> new HashSet<>());
		treeNodes.add(node);
		return node;
	}

	@After
	public void tearDown() throws Exception {
		semaphore = false;
	}

	@Test
	public void rootObservable() throws Exception {
		Set<String> set = new HashSet<>();
		set.add(ch00.toString());
		set.add(ch0.toString());
		set.add(ch120.toString());



		treeNodeFilter.rootObservable(treeStructure, set)
				.subscribe(tuple3 -> {
					assertNotNull(tuple3);
					TreeNode root = tuple3._1;

					assertEquals(root.toString(), root.toString());

					DefaultMutableTreeNode child0 = (DefaultMutableTreeNode) root.getChildAt(0);
					assertEquals(child0.toString(), ch0.toString());

					DefaultMutableTreeNode child00 = (DefaultMutableTreeNode) child0.getChildAt(0);
					assertEquals(child00.toString(), ch00.toString());

					DefaultMutableTreeNode child1 = (DefaultMutableTreeNode) root.getChildAt(1);
					assertEquals(child1.toString(), ch1.toString());

					DefaultMutableTreeNode child10 = (DefaultMutableTreeNode) child1.getChildAt(0);
					assertEquals(child10.toString(), ch10.toString());

					DefaultMutableTreeNode child11 = (DefaultMutableTreeNode) child1.getChildAt(1);
					assertEquals(child11.toString(), ch12.toString());

					DefaultMutableTreeNode child110 = (DefaultMutableTreeNode) child11.getChildAt(0);
					assertEquals(child110.toString(), ch120.toString());

					DefaultMutableTreeNode[] treeNodes = {child0, child00, child10, child110};
					List<DefaultMutableTreeNode> selectedNodes = tuple3._2;

					Assert.assertArrayEquals(treeNodes, selectedNodes.toArray());

					synchronized (lock) {
						semaphore = true;
						lock.notifyAll();
					}
				});

		synchronized (lock) {
			while (!semaphore) {
				lock.wait(10000);
			}
		}
	}

	@Test
	public void emptySetObservable() throws Exception {
		treeNodeFilter.rootObservable(treeStructure, Collections.emptySet())
				.subscribe(Assert::assertNull);
	}
}