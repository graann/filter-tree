package com.graann.laf;

import com.alee.laf.tree.WebTreeUI;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class CustomTreeUI extends WebTreeUI {
	private KeyHandler keyHandler;
	private JLabel label = new JLabel("BlaBla");

	public static ComponentUI createUI(JComponent x) {
		return new CustomTreeUI();
	}

	@Override
	protected void installComponents() {
		super.installComponents();
		tree.add(label);
	}

	protected KeyListener createKeyListener() {
		if(keyHandler == null) {
			keyHandler = new KeyHandler();
		}
		return keyHandler;
	}

	private class KeyHandler implements KeyListener {
		private String typedString = "";

		@Override
		public void keyTyped(KeyEvent e) {
			if(tree != null && tree.hasFocus() && tree.isEnabled()) {
				if (e.isAltDown() || isNavigationKey(e)) {
					return;
				}

				char c = e.getKeyChar();
				typedString += c;
				System.out.println("keyTyped: "+typedString);
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
	}
}
