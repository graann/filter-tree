package com.graann.tree.components;

import com.graann.common.Viewable;
import com.graann.tree.model.TreeModelController;
import com.graann.tree.model.TreeModelControllerFactory;
import com.graann.treeloader.TreeStructure;
import rx.Observable;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gromova on 22.09.17.
 */
public class TreeWidget implements Viewable<JComponent> {
	private Observable<String> patternObservable;
	private TreeModelControllerFactory modelControllerFactory;

	private TreeModelController treeModelController;

	private JTree tree;
	private JScrollPane scrollPane;
	private DefaultTreeModel model;

	public void setModelControllerFactory(TreeModelControllerFactory modelControllerFactory) {
		this.modelControllerFactory = modelControllerFactory;
	}

	public void setPatternObservable(Observable<String> patternObservable) {
		this.patternObservable = patternObservable;
	}

	@Override
	public JComponent getView() {
		return scrollPane;
	}

	public void initialize() {
		model = new DefaultTreeModel(null);

		treeModelController = modelControllerFactory.create(model, patternObservable);

		tree = new JTree(model);
		tree.setCellRenderer(new FilterTreeCellRenderer());
		tree.setExpandsSelectedPaths(true);

		scrollPane = new JScrollPane(tree);
	}

	@Override
	public void destroy() {
		treeModelController.destroy();
	}

	public void updateStructure(TreeStructure structure) {
		model.setRoot(structure.getRoot());
		treeModelController.updateStructure(structure);
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
