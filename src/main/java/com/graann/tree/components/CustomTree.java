package com.graann.tree.components;

import com.graann.common.Destroyable;
import com.graann.common.RxUtils;
import com.graann.tree.model.RootTreeNode;
import org.reactfx.util.Tuple2;
import rx.Observable;
import rx.Subscription;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Set;

/**
 * @author gromova on 26.09.17.
 */
public class CustomTree extends JTree implements Destroyable {
	private DefaultTreeModel model;
	private Set<TreeNode> opened = new HashSet<>();
	private Subscription filterSubscriber;
	private Subscription viewportAreaSubscription;

	private Observable<Rectangle> viewportArea;

	public CustomTree(Observable<Tuple2<String, TreeNode>> filterObservable, Observable<Rectangle> viewportArea) {
		super(new DefaultTreeModel(null));
		model = (DefaultTreeModel) getModel();
		this.viewportArea = viewportArea;

		setCellRenderer(new FilterTreeCellRenderer());
		setExpandsSelectedPaths(true);

		filterSubscriber = filterObservable.subscribe(t -> updateModel(t._1, t._2));
	}

	public void updateModel(String pattern, TreeNode root) {
		RxUtils.unsubscribe(viewportAreaSubscription);
		opened.clear();
		model.setRoot(root);

		boolean isRootTreeNode = root instanceof RootTreeNode;
		if (isRootTreeNode) {
			RootTreeNode rootTreeNode = (RootTreeNode) root;
			for (DefaultMutableTreeNode next : rootTreeNode.getSelectedNodes()) {
				String s = next.toString();
				String res = "<html>" + s.replace(pattern, "<font color='red'>" + pattern + "</font>") + "</html>";
				next.setUserObject(res);
			}
		}

		if(pattern != null && !pattern.isEmpty()) {
			viewportAreaSubscription = viewportArea.subscribe(visibleRectangle -> {
				final int firstRow = getClosestRowForLocation(visibleRectangle.x, visibleRectangle.y);
				int lastRow = getClosestRowForLocation(visibleRectangle.x, visibleRectangle.y + visibleRectangle.height);
				if (lastRow == -1 || firstRow == -1) {
					return;
				}
				expandNodes(firstRow, lastRow);
			});
		}
	}

	@Override
	public void destroy() {
		RxUtils.unsubscribe(filterSubscriber);
	}

	private void expandNodes(int startingIndex, int stopIndex) {
		for (int i = startingIndex; i <= stopIndex; i++) {

			TreePath pathForRow = getPathForRow(i);
			TreeNode node = (TreeNode) pathForRow.getLastPathComponent();
			if (!opened.contains(node)) {
				opened.add(node);
				expandRow(i);
			}
		}
	}

}
