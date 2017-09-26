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

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.Rectangle;
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
	private Subscription lockSubscriber;

	private TreeModelControllerFactory modelControllerFactory;

	private TreeModelController treeModelController;

	private JTree tree;
	private JScrollPane scrollPane;
	private DefaultTreeModel model;

	private BehaviorSubject<Boolean> verticalScrollObservable = BehaviorSubject.create();

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

		scrollPane.getVerticalScrollBar().addAdjustmentListener(e -> verticalScrollObservable.onNext(true));

		patternObservable.switchMap(s -> {
			if (s == null || s.isEmpty()) {
				return Observable.just(false);
			}

			return treeModelController.getUpdateObservable().switchMap(aVoid -> {
				expandVisible();
				return verticalScrollObservable
						.throttleLast(200, TimeUnit.MILLISECONDS);
			});
		}).subscribeOn(Schedulers.from(SwingUtilities::invokeLater)).subscribe(b -> {
			if (b) {
				expandVisible();
			}
		});
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

	private boolean lock;
	private void expandVisible() {
		if (lock) {
			return;
		}
		RxUtils.unsubscribe(lockSubscriber);
		lock = true;

		final Rectangle visibleRectangle = scrollPane.getViewport().getViewRect();
		final int firstRow = tree.getClosestRowForLocation(visibleRectangle.x, visibleRectangle.y);
		int lastRow = tree.getClosestRowForLocation(visibleRectangle.x, visibleRectangle.y + visibleRectangle.height);
		expandNodes(tree, firstRow, lastRow);

		lockSubscriber = Observable.interval(100, TimeUnit.MILLISECONDS).subscribe(aLong -> {
			if (lock) {
				lock = false;
				RxUtils.unsubscribe(lockSubscriber);
			}
		});
	}

	private static void expandNodes(JTree tree, int startingIndex, int stopIndex) {
		for (int i = startingIndex; i <= stopIndex; i++) {
			tree.expandRow(i);
		}
	}
}
