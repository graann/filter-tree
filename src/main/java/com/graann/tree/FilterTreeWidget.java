package com.graann.tree;

import com.graann.common.Viewable;
import com.graann.filter.Filtrator;
import com.graann.tree.model.FilterTreeModelWrapper;
import com.graann.treeloader.TreeLoader;
import net.miginfocom.swing.MigLayout;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author gromova on 20.09.17.
 */
public class FilterTreeWidget implements Viewable<JComponent> {
	private Subscription subscribe;

	private JPanel panel;
	private JTree tree;
	private JScrollPane scrollPane;

	private JTextField jTextField = new JTextField();
	private JButton button = new JButton("expand");

	private FilterTreeModelWrapper factory;

	private TreeLoader loader;

	public void setLoader(TreeLoader loader) {
		this.loader = loader;
	}

	public void setModelWrapper(FilterTreeModelWrapper factory) {
		this.factory = factory;
	}

	public JComponent getView() {
		return panel;
	}

	public void initialize() {

		panel = new JPanel(new MigLayout("fill, wrap 2", "[min!][]", "[min!][]"));

		panel.setPreferredSize(new Dimension(800, 600));
		panel.add(jTextField, "wmin 100");

		BehaviorSubject<String> patternObservable = BehaviorSubject.create();

		jTextField.addActionListener(e -> {
			patternObservable.onNext(jTextField.getText());
		});


		button.addActionListener(e -> expandNodes());
		panel.add(button);


		subscribe = loader.loadTreeStructure()
				.subscribeOn(Schedulers.from(SwingUtilities::invokeLater))
				.subscribe(structure -> {

					Filtrator filtrator = new Filtrator();
					filtrator.setMap(structure.getTreemap());
					filtrator.setRoot(structure.getRoot());
					filtrator.setPatternObservable(patternObservable);
					Observable<TreeModel> model = filtrator.getModel();

					TreeModel filterTreeModel = factory.wrap(model, new DefaultTreeModel(structure.getRoot()));

					tree = new JTree(filterTreeModel);
					tree.setExpandsSelectedPaths(true);

					scrollPane = new JScrollPane(tree);
					panel.add(scrollPane, "grow, span 2");
				});
	}

	@Override
	public void destroy() {
		if (subscribe != null && !subscribe.isUnsubscribed()) {
			subscribe.unsubscribe();
		}
	}

	public void expandNodes() {
		expandVisible(tree);
	}

	private void expandVisible(JTree tree) {

		JViewport viewport = scrollPane.getViewport();
		Point point = viewport.getViewPosition();

		TreePath firstPath = tree.getClosestPathForLocation(point.x, point.y);
		int firstIndex = tree.getRowForPath(firstPath);

		TreePath lastPath = tree.getClosestPathForLocation(point.x, point.y + viewport.getWidth());
		int lastIndex = tree.getRowForPath(lastPath);

		expandNodes(tree, firstIndex, lastIndex);
	}

	private static void expandNodes(JTree tree, int startingIndex, int stopIndex) {
		for (int i = startingIndex; i <= stopIndex; i++) {
			tree.expandRow(i);
		}
	}

	/**
	 * TODO move to model
	 */

	public static TreePath getPath(TreeNode treeNode) {
		List<Object> nodes = new ArrayList<>();
		if (treeNode != null) {
			nodes.add(treeNode);
			treeNode = treeNode.getParent();
			while (treeNode != null) {
				nodes.add(0, treeNode);
				treeNode = treeNode.getParent();
			}
		}

		return nodes.isEmpty() ? null : new TreePath(nodes.toArray());
	}

	private static List<TreeNode> getAllFinalNode(TreeNode node) {
		List<TreeNode> leafNodes = new ArrayList<>();

		if (node.isLeaf()) {
			leafNodes.add(node.getParent());
		} else {
			Enumeration children = node.children();
			while (children.hasMoreElements()) {
				leafNodes.addAll(getAllFinalNode((TreeNode) children.nextElement()));
			}
		}
		return leafNodes;
	}
}

