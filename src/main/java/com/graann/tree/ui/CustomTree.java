package com.graann.tree.ui;

import com.graann.common.Destroyable;
import com.graann.common.RxUtils;
import com.graann.styling.ColorScheme;
import com.graann.tree.filter.FilteredState;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;
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
import java.util.function.BiConsumer;

/**
 * @author gromova on 26.09.17.
 */
public class CustomTree extends JTree implements Destroyable {
	private final DefaultTreeModel model;
	private final Set<TreeNode> opened = new HashSet<>();
	private Subscription filterSubscriber;
	private Subscription viewportAreaSubscription;

	private FilteredState filteredState;
	private SelectionController selectionController;

	private BehaviorSubject<Rectangle> viewportAreaObservable;
	private BiConsumer<Integer, Integer> counterConsumer;
	private Observable<FilteredState> filterObservable;

	void setViewportAreaObservable(BehaviorSubject<Rectangle> viewportArea) {
		this.viewportAreaObservable = viewportArea;
	}

	void setFilterObservable(Observable<FilteredState> filterObservable) {
		this.filterObservable = filterObservable;
	}

	void setCounterConsumer(BiConsumer<Integer, Integer> counterConsumer) {
		this.counterConsumer = counterConsumer;
	}

	CustomTree() {
		super(new DefaultTreeModel(null));
		model = (DefaultTreeModel) getModel();
	}

	void init() {
		selectionController = new SelectionController(this);
		filterSubscriber = filterObservable
				.observeOn(Schedulers.from(SwingUtilities::invokeLater))
				.subscribe(state -> {
					this.filteredState = state;
					opened.clear();
					getSelectionModel().clearSelection();
					model.setRoot(filteredState.getRoot());
					selectionController.setSuitable(filteredState.getFilteredNodes());

					RxUtils.unsubscribe(viewportAreaSubscription);

					counterConsumer.accept(filteredState.getTotal(), filteredState.getFiltered());

					if (!filteredState.getFilteredNodes().isEmpty()) {
						updateSuitable(filteredState.getFilteredNodes());
						expandVisible(viewportAreaObservable.getValue());

						viewportAreaSubscription = viewportAreaObservable
								.throttleLast(80, TimeUnit.MILLISECONDS)
								.subscribe(this::expandVisible);
					}
				});
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
	private void updateSuitable(List<DefaultMutableTreeNode> suitable) {
		for (DefaultMutableTreeNode next : suitable) {
			String s = next.toString();
			String res = Utils.replacePattern(filteredState.getPattern(), s, ColorScheme.PATTERN);
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
