package com.graann.tree.ui;

import com.graann.common.Destroyable;
import com.graann.common.RxUtils;
import com.graann.styling.ColorScheme;
import com.graann.tree.filter.RootTreeNode;
import org.reactfx.util.Tuple2;
import rx.Observable;
import rx.Subscription;
import rx.subjects.BehaviorSubject;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author gromova on 26.09.17.
 */
public class CustomTree extends JTree implements Destroyable {
	private final DefaultTreeModel model;
	private final Set<TreeNode> opened = new HashSet<>();
	private final Subscription filterSubscriber;
	private Subscription viewportAreaSubscription;

	private String pattern;
	private List<DefaultMutableTreeNode> suitable = Collections.emptyList();
	private final SelectionController selectionController;

	private final BehaviorSubject<Rectangle> viewportArea;
	private final Consumer<Integer> filteredCounterConsumer;

	CustomTree(Observable<Tuple2<String, TreeNode>> filterObservable, BehaviorSubject<Rectangle> viewportArea, Consumer<Integer> filteredCounterConsumer) {
		super(new DefaultTreeModel(null));
		model = (DefaultTreeModel) getModel();
		this.viewportArea = viewportArea;
		selectionController = new SelectionController(this);
		this.filteredCounterConsumer = filteredCounterConsumer;

		filterSubscriber = filterObservable.subscribe(t -> updateModel(t._1, t._2));
	}

	private void updateModel(String pattern, TreeNode root) {
		this.pattern = pattern;
		boolean isRootTreeNode = root instanceof RootTreeNode;
		suitable = isRootTreeNode ? ((RootTreeNode) root).getSelectedNodes() : Collections.emptyList();

		opened.clear();
		getSelectionModel().clearSelection();
		model.setRoot(root);
		selectionController.setSuitable(suitable);

		RxUtils.unsubscribe(viewportAreaSubscription);

		filteredCounterConsumer.accept(isRootTreeNode ? ((RootTreeNode) root).getFilteredCount() : null);

		if (isRootTreeNode) {
			updateSuitable();
			expandVisible(viewportArea.getValue());

			viewportAreaSubscription = viewportArea
					.throttleLast(80, TimeUnit.MILLISECONDS)
					.subscribe(this::expandVisible);
		} else if (root == null) {
			filteredCounterConsumer.accept(0);
		} else {
			filteredCounterConsumer.accept(null);
		}
	}

	private void expandVisible(Rectangle visibleRectangle) {
		final int firstRow = getClosestRowForLocation(visibleRectangle.x, visibleRectangle.y);
		int rowCount = visibleRectangle.height / rowHeight;

		if (firstRow == -1) {
			return;
		}
		expandNodes(firstRow, firstRow + rowCount);
	}


	void previousSuitable() {
		selectionController.previousSuitable();
	}

	void nextSuitable() {
		selectionController.nextSuitable();
	}


	/**
	 * used instead of {@link #convertValueToText} to avoid leaks
	 */
	private void updateSuitable() {
		for (DefaultMutableTreeNode next : suitable) {
			String s = next.toString();
			String res = Utils.replacePattern(pattern, s, ColorScheme.PATTERN);
			next.setUserObject(res);
		}
	}


	@Override
	public void destroy() {
		RxUtils.unsubscribe(filterSubscriber);
		RxUtils.unsubscribe(viewportAreaSubscription);
	}

	private void expandNodes(int startingIndex, int stopIndex) {
		for (int i = startingIndex; i <= stopIndex && i <= getRowCount(); i++) {

			TreePath pathForRow = getPathForRow(i);
			if (pathForRow == null) {
				return;
			}

			TreeNode node = (TreeNode) pathForRow.getLastPathComponent();
			if (!opened.contains(node)) {
				opened.add(node);
				expandRow(i);
			}
		}
	}

}
