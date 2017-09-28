package com.graann.laf;

import com.alee.laf.tree.WebTreeUI;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.event.KeyListener;

public class CustomTreeUI extends WebTreeUI {
	public static ComponentUI createUI(JComponent x) {
		return new CustomTreeUI();
	}

	protected KeyListener createKeyListener() {
		return null;
	}
}
