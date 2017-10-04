package com.graann.tree.components;

import com.graann.common.Destroyable;
import com.graann.common.RxUtils;
import com.graann.components.Utils;
import com.graann.styling.ColorScheme;
import com.graann.tree.model.RootTreeNode;
import org.reactfx.util.Tuple2;
import rx.Observable;
import rx.Subscription;

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
import java.util.function.Consumer;

/**
 * @author gromova on 26.09.17.
 */
public class CustomTree extends JTree implements Destroyable {
	private DefaultTreeModel model;
	private Set<TreeNode> opened = new HashSet<>();
	private Subscription filterSubscriber;
	private Subscription viewportAreaSubscription;

	private String pattern;
	private List<DefaultMutableTreeNode> suitables = Collections.emptyList();
	private SelectionController selectionController;

	private Observable<Rectangle> viewportArea;
	private Consumer<Integer> filteredCounterConsumer;

	private boolean lock;

	CustomTree(Observable<Tuple2<String, TreeNode>> filterObservable, Observable<Rectangle> viewportArea, Consumer<Integer> filteredCounterConsumer) {
		super(new DefaultTreeModel(null));
		model = (DefaultTreeModel) getModel();
		this.viewportArea = viewportArea;
		selectionController = new SelectionController(this);
		this.filteredCounterConsumer = filteredCounterConsumer;

		filterSubscriber = filterObservable.subscribe(t -> updateModel(t._1, t._2));
	}

	void updateModel(String pattern, TreeNode root) {
		lock = true;
		this.pattern = pattern;
		boolean isRootTreeNode = root instanceof RootTreeNode;
		suitables = isRootTreeNode ? ((RootTreeNode) root).getSelectedNodes() : Collections.emptyList();

		opened.clear();
		getSelectionModel().clearSelection();
		model.setRoot(root);
		selectionController.setSuitables(suitables);

		RxUtils.unsubscribe(viewportAreaSubscription);

		if (isRootTreeNode) {
			filteredCounterConsumer.accept(((RootTreeNode) root).getFilteredCount());
			updateSuitables();
			lock = false;
			viewportAreaSubscription = viewportArea.subscribe(visibleRectangle -> {
				final int firstRow = getClosestRowForLocation(visibleRectangle.x, visibleRectangle.y);
				int rowCount = visibleRectangle.height / rowHeight;
				int lastRow = getClosestRowForLocation(visibleRectangle.x, visibleRectangle.y + visibleRectangle.height);

				if (firstRow == -1) {
					return;
				}
				expandNodes(firstRow, Math.max(lastRow, rowCount));
			});
		} else if (root == null) {
			filteredCounterConsumer.accept(0);
		} else {
			filteredCounterConsumer.accept(null);
		}
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
	private void updateSuitables() {
		for (DefaultMutableTreeNode next : suitables) {
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
		for (int i = startingIndex; i <= stopIndex && i <= getRowCount() && !lock; i++) {

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
