package com.graann.tree.components;

import com.graann.common.Viewable;
import com.graann.styling.ColorScheme;
import com.graann.styling.FontIcon;
import com.graann.styling.IconFontSymbols;
import com.graann.styling.LAFUtils;
import com.graann.tree.model.TreeFilter;
import com.graann.tree.model.TreeFilterFactory;
import com.graann.treeloader.TreeStructure;
import net.miginfocom.swing.MigLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.subjects.BehaviorSubject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * @author gromova on 22.09.17.
 */
public class TreeWidget implements Viewable<JComponent> {
	private static final Logger LOG = LoggerFactory.getLogger(TreeWidget.class);

	private BehaviorSubject<String> patternObservable = BehaviorSubject.create();

	private TreeFilterFactory modelControllerFactory;

	private TreeFilter treeFilter;

	private CustomTree tree;
	private JLabel shownLabel;
	private JLabel filterLabel;
	private JLabel totalLabel;
	private JScrollPane scrollPane;
	private JPanel panel;

	private KeyHandler keyHandler;

	private BehaviorSubject<Rectangle> verticalScrollObservable = BehaviorSubject.create();

	void setModelControllerFactory(TreeFilterFactory modelControllerFactory) {
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
		JPanel infopane = new JPanel(new MigLayout("flowx, fillx, ins 0 0 5 0", "[pref!]push[min!][min!]"));

		filterLabel = new JLabel();
		filterLabel.setForeground(ColorScheme.MAINT_TEXT);
		totalLabel = new JLabel();
		totalLabel.setIcon(FontIcon.builder().symbol(IconFontSymbols.COUNT.getString())
				.color(ColorScheme.LEAF_ICON).build());
		shownLabel = new JLabel();
		shownLabel.setIcon(FontIcon.builder().symbol(IconFontSymbols.FUNNEL.getString())
				.fontSize(14)
				.color(ColorScheme.LEAF_ICON).build());

		infopane.add(filterLabel, "w 220!");
		infopane.add(totalLabel);
		infopane.add(shownLabel);

		panel = new JPanel(new MigLayout("flowy, ins 0, gap 0, fill", "", "[min!][]"));
		panel.add(infopane, "growx");

		treeFilter = modelControllerFactory.create(patternObservable);

		tree = new CustomTree(treeFilter.filteredStateObservable(), verticalScrollObservable, this::updateFilteredCounter);

		scrollPane = new JScrollPane(tree);
		scrollPane.getVerticalScrollBar().addAdjustmentListener(e -> verticalScrollObservable.onNext(scrollPane.getViewport().getViewRect()));

		panel.add(scrollPane, "grow");

		tree.addFocusListener(getHandler());
		scrollPane.addFocusListener(getHandler());
		updateFilterLabel();
	}

	private void updateFilteredCounter(Integer count) {
		shownLabel.setText(count != null ? String.valueOf(count) : totalLabel.getText());
		filterLabel.setForeground(count != null && count == 0 ? ColorScheme.PATTERN : ColorScheme.MAINT_TEXT);
	}

	void updateStructure(TreeStructure structure) {
		totalLabel.setText(String.valueOf(structure.getCount()));
		shownLabel.setText(String.valueOf(structure.getCount()));
		tree.updateModel(null, structure.getRoot());
		treeFilter.updateStructure(structure);
	}

	private void updatePattern(String typedString) {
		filterLabel.setText(typedString);
		patternObservable.onNext(typedString);
		updateFilterLabel();
		LAFUtils.setTooltipIfNeeded(filterLabel);
	}

	private void updateFilterLabel() {
		filterLabel.setVisible(focused || !filterLabel.getText().isEmpty());
		filterLabel.setIcon(focused ? FontIcon.builder().symbol(IconFontSymbols.SEARCH.getString())
				.color(ColorScheme.DEFAULT_ICON).build() : FontIcon.builder().symbol(IconFontSymbols.SEARCH.getString())
				.color(ColorScheme.DISABLED).build() );

	}

	@Override
	public void destroy() {
		treeFilter.destroy();
	}

	private boolean focused;

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
			focused = true;
			updateFilterLabel();

			tree.addKeyListener(getHandler());
			scrollPane.addKeyListener(getHandler());
		}

		@Override
		public void focusLost(FocusEvent e) {
			focused = false;
			updateFilterLabel();

			tree.removeKeyListener(getHandler());
			scrollPane.removeKeyListener(getHandler());

		}
	}
}
