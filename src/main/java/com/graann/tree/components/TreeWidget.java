package com.graann.tree.components;

import com.graann.common.Viewable;
import com.graann.tree.model.TreeModelController;
import com.graann.tree.model.TreeModelControllerFactory;
import com.graann.treeloader.TreeStructure;
import net.miginfocom.swing.MigLayout;
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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author gromova on 22.09.17.
 */
public class TreeWidget implements Viewable<JComponent> {
	private static final Logger LOG = LoggerFactory.getLogger(TreeWidget.class);

	private BehaviorSubject<String> patternObservable = BehaviorSubject.create();

	private TreeModelControllerFactory modelControllerFactory;

	private TreeModelController treeModelController;

	private CustomTree tree;
	private JLabel infoLabel;
	private JScrollPane scrollPane;
	private JPanel panel;
	private JPanel infoPane;

	private DefaultTreeModel model;
	private KeyHandler keyHandler;

	private Set<TreeNode> opened = new HashSet<>();

	private BehaviorSubject<Boolean> verticalScrollObservable = BehaviorSubject.create();

	void setModelControllerFactory(TreeModelControllerFactory modelControllerFactory) {
		this.modelControllerFactory = modelControllerFactory;
	}

	private KeyHandler getHandler() {
		if (keyHandler == null) {
			keyHandler = new KeyHandler();
		}
		return keyHandler;
	}

	@Override
	public JComponent getView() {
		return panel;
	}

	void initialize() {
		model = new DefaultTreeModel(null);
		tree = new CustomTree(model);
		tree.addKeyListener(getHandler());
		tree.addFocusListener(getHandler());

		infoPane = new JPanel(new MigLayout("ins 0, gap 0"));
		infoLabel = new JLabel();
		infoPane.add(new JLabel("Search: "));
		infoPane.add(infoLabel);

		panel = new JPanel(new MigLayout("flowy, ins 0, gap 0, fill", "", "[min!][]"));
		panel.add(infoPane);
		scrollPane = new JScrollPane(tree);
		panel.add(scrollPane, "grow");

		treeModelController = modelControllerFactory.create(model, patternObservable);

		scrollPane.getVerticalScrollBar().addAdjustmentListener(e -> verticalScrollObservable.onNext(true));

		patternObservable.switchMap(s -> {
			infoLabel.setText(s);

			if (s == null || s.isEmpty()) {
				return Observable.just(false);
			}

			opened.clear();

			return treeModelController.getUpdateObservable().switchMap(b -> {
				if (!b) {
					return Observable.just(false);
				}

				expandVisible();
				return verticalScrollObservable.throttleLast(70, TimeUnit.MILLISECONDS);
			});
		}).observeOn(Schedulers.from(SwingUtilities::invokeLater)).subscribe(b -> {
			if (b) {
				expandVisible();
			}
		});


	}

	void updateStructure(TreeStructure structure) {
		model.setRoot(structure.getRoot());
		treeModelController.updateStructure(structure);
	}

	private void expandVisible() {
		final Rectangle visibleRectangle = scrollPane.getViewport().getViewRect();
		final int firstRow = tree.getClosestRowForLocation(visibleRectangle.x, visibleRectangle.y);
		int lastRow = tree.getClosestRowForLocation(visibleRectangle.x, visibleRectangle.y + visibleRectangle.height);
		if (lastRow == -1 || firstRow == -1) {
			return;
		}
		expandNodes(firstRow, lastRow);
	}

	private void expandNodes(int startingIndex, int stopIndex) {
		for (int i = startingIndex; i <= stopIndex; i++) {

			TreePath pathForRow = tree.getPathForRow(i);
			TreeNode node = (TreeNode) pathForRow.getLastPathComponent();
			if (!opened.contains(node)) {
				opened.add(node);
				tree.expandRow(i);
			}
		}
	}

	@Override
	public void destroy() {
		treeModelController.destroy();
	}

	private class KeyHandler implements KeyListener, FocusListener {
		private String typedString = "";

		@Override
		public void keyTyped(KeyEvent e) {
			if (tree != null && tree.hasFocus() && tree.isEnabled()) {
				if (e.isAltDown() || isNavigationKey(e)) {
					return;
				}

				char c = e.getKeyChar();
				if (c == KeyEvent.VK_BACK_SPACE) {
					if (typedString.length() > 0) {
						typedString = typedString.substring(0, typedString.length() - 1);
					}
				} else {
					typedString += c;
				}

				patternObservable.onNext(typedString);
			}

		}

		@Override
		public void keyPressed(KeyEvent e) {

		}

		@Override
		public void keyReleased(KeyEvent e) {

		}

		private boolean isNavigationKey(KeyEvent event) {
			InputMap inputMap = tree.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
			KeyStroke key = KeyStroke.getKeyStrokeForEvent(event);

			return inputMap != null && inputMap.get(key) != null;
		}

		@Override
		public void focusGained(FocusEvent e) {
			infoPane.setVisible(true);
		}

		@Override
		public void focusLost(FocusEvent e) {
			infoPane.setVisible(false);
		}
	}
}
