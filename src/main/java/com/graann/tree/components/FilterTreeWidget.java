package com.graann.tree.components;

import com.graann.common.NumberFormatter;
import com.graann.common.Viewable;
import com.graann.components.FilterBox;
import com.graann.components.Messages;
import com.graann.components.WidgetFactory;
import com.graann.styling.ColorScheme;
import com.graann.styling.FontIcon;
import com.graann.styling.IconFontSymbols;
import com.graann.tree.model.TreeFilter;
import com.graann.tree.model.TreeFilterFactory;
import com.graann.treeloader.TreeStructure;
import net.miginfocom.swing.MigLayout;
import rx.subjects.BehaviorSubject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * @author gromova on 22.09.17.
 */
public class FilterTreeWidget implements Viewable<JComponent> {
	private NumberFormatter formatter = new NumberFormatter();
	private BehaviorSubject<String> patternObservable = BehaviorSubject.create();

	private TreeFilterFactory treeFilterFactory;
	private TreeFilter treeFilter;

	private CustomTree tree;
	private JLabel shownLabel;
	private FilterBox filterLabel;
	private JLabel totalLabel;
	private JLabel nothingFound;
	private JScrollPane scrollPane;
	private JPanel panel;

	private KeyHandler keyHandler;

	private BehaviorSubject<Rectangle> verticalScrollObservable = BehaviorSubject.create();
	private AdjustmentListener verticalScrollBarListener = e -> verticalScrollObservable.onNext(scrollPane.getViewport().getViewRect());

	void setTreeFilterFactory(TreeFilterFactory treeFilterFactory) {
		this.treeFilterFactory = treeFilterFactory;
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

		JPanel infopane = new JPanel(new MigLayout("flowx, fillx, ins 0 0 5 0", "[pref!]push[min!][min!]"));

		filterLabel = new FilterBox();
		totalLabel = new JLabel();
		shownLabel = new JLabel();
		nothingFound = WidgetFactory.createWarningLabel(Messages.NOTHING_FOUND);

		totalLabel.setIcon(FontIcon.builder().symbol(IconFontSymbols.COUNT.getString())
				.color(ColorScheme.LEAF_ICON).build());
		shownLabel.setIcon(FontIcon.builder().symbol(IconFontSymbols.FUNNEL.getString())
				.fontSize(14)
				.color(ColorScheme.LEAF_ICON).build());

		infopane.add(filterLabel.getView(), "w 220!");
		infopane.add(totalLabel);
		infopane.add(shownLabel);

		panel = new JPanel(new MigLayout("flowy, ins 0, gap 0, fill, hidemode 3", "", "[min!][]"));
		panel.add(infopane, "growx");

		treeFilter = treeFilterFactory.create(patternObservable);

		tree = new CustomTree(treeFilter.filteredStateObservable(), verticalScrollObservable, this::updateFilteredCounter);

		scrollPane = new JScrollPane(tree);
		scrollPane.getVerticalScrollBar().addAdjustmentListener(verticalScrollBarListener);

		panel.add(nothingFound, "pos 10 30");
		panel.add(scrollPane, "grow");

		tree.addFocusListener(getHandler());
		scrollPane.addFocusListener(getHandler());
		nothingFound.setVisible(false);
	}

	private void updateFilteredCounter(Integer count) {
		shownLabel.setText(count != null ? formatter.format(count) : totalLabel.getText());
		boolean empty = count != null && count == 0;

		nothingFound.setVisible(empty);
		filterLabel.setState(empty ? FilterBox.State.ERROR : FilterBox.State.SUCCESS);
	}

	void updateStructure(TreeStructure structure) {
		totalLabel.setText(formatter.format(structure.getCount()));
		updateFilteredCounter(null);
		tree.updateModel(null, structure.getRoot());
		treeFilter.updateStructure(structure);
	}

	private void updatePattern(String typedString) {
		filterLabel.setText(typedString);
		patternObservable.onNext(typedString);
	}

	@Override
	public void destroy() {
		treeFilter.destroy();
		scrollPane.getVerticalScrollBar().removeAdjustmentListener(verticalScrollBarListener);
	}

	private class KeyHandler implements KeyListener, FocusListener {
		private String typedString = "";

		@Override
		public void keyTyped(KeyEvent e) {
			if (tree != null && tree.isEnabled()) {
				if (e.isAltDown() || e.isControlDown() || isNavigationKey(e)) {
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

				updatePattern(typedString);

			}
		}

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_F3) {
				if (e.isShiftDown()) {
					tree.previousSuitable();
				} else {
					tree.nextSuitable();
				}
			}
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
			tree.addKeyListener(getHandler());
			scrollPane.addKeyListener(getHandler());
		}

		@Override
		public void focusLost(FocusEvent e) {
			tree.removeKeyListener(getHandler());
			scrollPane.removeKeyListener(getHandler());

		}
	}
}
