package com.graann.tree.components;

import com.graann.common.Viewable;
import com.graann.tree.model.TreeModelController;
import com.graann.tree.model.TreeModelControllerFactory;
import com.graann.treeloader.TreeStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.schedulers.Schedulers;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.util.List;

/**
 * @author gromova on 22.09.17.
 */
public class TreeWidget implements Viewable<JComponent> {
	private static final Logger LOG = LoggerFactory.getLogger(TreeWidget.class);

	private Observable<String> patternObservable;

	private TreeModelControllerFactory modelControllerFactory;

	private TreeModelController treeModelController;

	private CustomTree tree;
	private JScrollPane scrollPane;
	private DefaultTreeModel model;

	void setModelControllerFactory(TreeModelControllerFactory modelControllerFactory) {
		this.modelControllerFactory = modelControllerFactory;
	}

	void setPatternObservable(Observable<String> patternObservable) {
		this.patternObservable = patternObservable;
	}

	@Override
	public JComponent getView() {
		return scrollPane;
	}

	void initialize() {
		model = new DefaultTreeModel(null);

		tree = new CustomTree(model);

		scrollPane = new JScrollPane(tree);
		treeModelController = modelControllerFactory.create(model, patternObservable);

		treeModelController
				.getUpdateObservable()
				.observeOn(Schedulers.from(SwingUtilities::invokeLater))
				.subscribe(defaultMutableTreeNodes -> {
				//	expandVisible();
			});
	}

	private void updateExpandSubscription(List<DefaultMutableTreeNode> list) {

	}

	@Override
	public void destroy() {
		treeModelController.destroy();
	}

	void updateStructure(TreeStructure structure) {
		model.setRoot(structure.getRoot());
		treeModelController.updateStructure(structure);
	}

	public void expandVisible() {
		final Rectangle visibleRectangle = scrollPane.getViewport().getViewRect();
		final int firstRow = tree.getClosestRowForLocation(visibleRectangle.x, visibleRectangle.y);
		int lastRow = tree.getClosestRowForLocation(visibleRectangle.x, visibleRectangle.y + visibleRectangle.height);
		tree.expandNodes(firstRow, lastRow);
	}


}
