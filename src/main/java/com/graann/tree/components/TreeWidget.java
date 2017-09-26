package com.graann.tree.components;

import com.graann.common.RxUtils;
import com.graann.common.Viewable;
import com.graann.tree.model.TreeModelController;
import com.graann.tree.model.TreeModelControllerFactory;
import com.graann.treeloader.TreeStructure;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author gromova on 22.09.17.
 */
public class TreeWidget implements Viewable<JComponent>, AdjustmentListener {
	private Observable<String> patternObservable;
	private TreeModelControllerFactory modelControllerFactory;

	private TreeModelController treeModelController;

	private JTree tree;
	private JScrollPane scrollPane;
	private DefaultTreeModel model;

	private Subscription expandVisibleSubscription;

	private BehaviorSubject<AdjustmentEvent> verticalScrollObservable = BehaviorSubject.create();

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

		tree = new JTree(model);
		tree.setCellRenderer(new FilterTreeCellRenderer());
		tree.setExpandsSelectedPaths(true);

		scrollPane = new JScrollPane(tree);
		treeModelController = modelControllerFactory.create(model, patternObservable);

/*
		scrollPane.getVerticalScrollBar().addAdjustmentListener(e -> verticalScrollObservable.onNext(e));

		patternObservable.subscribe(s -> {
			RxUtils.unsubscribe(expandVisibleSubscription);
			if(s !=null && !s.isEmpty()) {
				expandVisibleSubscription = verticalScrollObservable
						.debounce(100, TimeUnit.MILLISECONDS)
						.subscribe(adjustmentEvent -> expandVisible());
			}
		});
*/


	}

	@Override
	public void destroy() {
		treeModelController.destroy();
	}

	void updateStructure(TreeStructure structure) {
		model.setRoot(structure.getRoot());
		treeModelController.updateStructure(structure);
	}

	private List<TreeNode> getVisibleNodes() {
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

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		expandVisible();
	}

	private boolean lock = false;

	private void expandVisible() {
		if(lock) return;
		lock = true;
		System.out.println("expand");
		final Rectangle visibleRectangle = scrollPane.getViewport().getViewRect();
		final int firstRow = tree.getClosestRowForLocation(visibleRectangle.x, visibleRectangle.y);
		final int lastRow = tree.getClosestRowForLocation(visibleRectangle.x, visibleRectangle.y + visibleRectangle.height);

		expandNodes(tree, firstRow, lastRow);
		lock = false;
	}

	private static void expandNodes(JTree tree, int startingIndex, int stopIndex) {
		for (int i = startingIndex; i <= stopIndex; i++) {
			tree.expandRow(i);
		}
	}
}
