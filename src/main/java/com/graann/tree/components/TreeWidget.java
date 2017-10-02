package com.graann.tree.components;

import com.graann.common.Viewable;
import com.graann.styling.ColorScheme;
import com.graann.styling.FontIcon;
import com.graann.styling.IconFontSymbols;
import com.graann.tree.model.TreeFilter;
import com.graann.tree.model.TreeFilterFactory;
import com.graann.treeloader.TreeStructure;
import net.miginfocom.swing.MigLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.subjects.BehaviorSubject;

import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import java.awt.Rectangle;
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
	private JLabel infoLabel;
	private JScrollPane scrollPane;
	private JPanel panel;
	private JPanel infoPane;

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

		infoPane = new JPanel(new MigLayout("ins 0, gap 7"));
		infoLabel = new JLabel();
		infoLabel.setForeground(ColorScheme.PATTERN);
		infoPane.add(new JLabel(FontIcon.builder().symbol(IconFontSymbols.SEARCH.getString())
				.color(ColorScheme.DEFAULT_ICON).build()));
		infoPane.add(infoLabel);

		panel = new JPanel(new MigLayout("flowy, ins 0, gap 0, fill", "", "[min!][]"));
		panel.add(infoPane);

		treeFilter = modelControllerFactory.create(patternObservable);

		tree = new CustomTree(treeFilter.filteredStateObservable(), verticalScrollObservable);

		scrollPane = new JScrollPane(tree);
		scrollPane.getVerticalScrollBar().addAdjustmentListener(e -> verticalScrollObservable.onNext(scrollPane.getViewport().getViewRect()));

		panel.add(scrollPane, "grow");

		tree.addFocusListener(getHandler());
		scrollPane.addFocusListener(getHandler());
		infoPane.setVisible(false);
	}

	void updateStructure(TreeStructure structure) {
		tree.updateModel(null, structure.getRoot());
		treeFilter.updateStructure(structure);
	}

	@Override
	public void destroy() {
		treeFilter.destroy();
	}


	private class KeyHandler implements KeyListener, FocusListener {
		private String typedString = "";

		@Override
		public void keyTyped(KeyEvent e) {
			if (tree != null && tree.isEnabled()) {
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

				infoLabel.setText(typedString);
				patternObservable.onNext(typedString);
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
			infoPane.setVisible(true);

			tree.addKeyListener(getHandler());
			scrollPane.addKeyListener(getHandler());
		}

		@Override
		public void focusLost(FocusEvent e) {
			infoPane.setVisible(false);

			tree.removeKeyListener(getHandler());
			scrollPane.removeKeyListener(getHandler());

		}
	}
}
