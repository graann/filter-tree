package com.graann.laf;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalTreeUI;
import java.awt.event.KeyListener;

public class CustomTreeUI extends MetalTreeUI {
	public static ComponentUI createUI(JComponent x) {
		return new CustomTreeUI();
	}

	protected KeyListener createKeyListener() {
		return null;
	}
}
