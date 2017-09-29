package com.graann.laf;

import com.seaglasslookandfeel.ui.SeaGlassTreeUI;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import java.awt.event.KeyListener;

public class CustomTreeUI extends SeaGlassTreeUI {
	public static ComponentUI createUI(JComponent x) {
		return new CustomTreeUI();
	}

	protected KeyListener createKeyListener() {
		return null;
	}
}
