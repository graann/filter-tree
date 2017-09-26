package com.graann.tree.components;

import com.graann.common.Viewable;
import com.graann.tree.model.TreeModelController;
import com.graann.tree.model.TreeModelControllerFactory;
import com.graann.treeloader.TreeStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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

	private Set<TreeNode> opened = new HashSet<>();

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

		tree = new CustomTree(model);

		scrollPane = new JScrollPane(tree);
		treeModelController = modelControllerFactory.create(model, patternObservable);

		scrollPane.getVerticalScrollBar().addAdjustmentListener(e -> verticalScrollObservable.onNext(true));

		patternObservable.switchMap(s -> {
			if (s == null || s.isEmpty()) {
				return Observable.just(false);
			}

			return treeModelController.getUpdateObservable().switchMap(aVoid -> {
				expandVisible();
				return verticalScrollObservable.throttleLast(100, TimeUnit.MILLISECONDS);
			});
		}).observeOn(Schedulers.from(SwingUtilities::invokeLater)).subscribe(b -> {
			if (b) {
				opened.clear();
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

	public void expandVisible() {
		final Rectangle visibleRectangle = scrollPane.getViewport().getViewRect();
		final int firstRow = tree.getClosestRowForLocation(visibleRectangle.x, visibleRectangle.y);
		int lastRow = tree.getClosestRowForLocation(visibleRectangle.x, visibleRectangle.y + visibleRectangle.height);
		expandNodes(firstRow, lastRow);
	}

	private void expandNodes(int startingIndex, int stopIndex) {
		for (int i = startingIndex; i <= stopIndex; i++) {
			/**TODO fix it!
			 *
			 */
			TreePath pathForRow = tree.getPathForRow(i);
			TreeNode lastPathObject = (TreeNode) pathForRow.getLastPathComponent();
			if (!opened.contains(lastPathObject)) {
				opened.add(lastPathObject);
				tree.expandRow(i);
			}
		}
	}
}
