package com.graann.tree.components;

import com.graann.common.Viewable;

import javax.swing.*;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gromova on 22.09.17.
 */
public class TreeWidget implements Viewable<JComponent> {
	private JTree tree;
	private JScrollPane scrollPane;

	private TreeModel model;

	public void setModel(TreeModel model) {
		this.model = model;
	}

	@Override
	public JComponent getView() {
		return scrollPane;
	}

	public void initialize() {
		tree = new JTree(model);
		tree.setCellRenderer(new FilterTreeCellRenderer());
		tree.setExpandsSelectedPaths(true);

		scrollPane = new JScrollPane(tree);
	}

	@Override
	public void destroy() {

	}

	public List<TreeNode> getVisibleNodes() {
		final Rectangle visibleRectangle = scrollPane.getViewport().getViewRect();
		final int firstRow = tree.getClosestRowForLocation(visibleRectangle.x, visibleRectangle.y);
		final int lastRow = tree.getClosestRowForLocation(visibleRectangle.x, visibleRectangle.y + visibleRectangle.height);
		List<TreeNode> resultList = new ArrayList<>();

		if (firstRow < 0 || lastRow < 0) {
			return resultList;
		}

		for (int currentRow = firstRow; currentRow <= lastRow; currentRow++) {
			TreePath currentPath = tree.getPathForRow(currentRow);
			Object lastPathObject = currentPath.getLastPathComponent();
			if (lastPathObject instanceof TreeNode) {
				resultList.add((TreeNode) lastPathObject);
			}
		}
		return resultList;
	}

	private void expandVisible() {
		final Rectangle visibleRectangle = scrollPane.getViewport().getViewRect();
		final int firstRow = tree.getClosestRowForLocation(visibleRectangle.x, visibleRectangle.y);
		final int lastRow = tree.getClosestRowForLocation(visibleRectangle.x, visibleRectangle.y + visibleRectangle.height);

		expandNodes(tree, firstRow, lastRow);
	}

	private static void expandNodes(JTree tree, int startingIndex, int stopIndex) {
		for (int i = startingIndex; i <= stopIndex; i++) {
			tree.expandRow(i);
		}
	}
}
