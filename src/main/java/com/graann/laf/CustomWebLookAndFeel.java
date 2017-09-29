package com.graann.laf;

import com.seaglasslookandfeel.SeaGlassLookAndFeel;

import javax.swing.UIDefaults;

public class CustomWebLookAndFeel extends SeaGlassLookAndFeel {
	@Override
	protected void initClassDefaults(UIDefaults table) {
		super.initClassDefaults(table);
		table.put("TreeUI", CustomTreeUI.class.getName());
	}

	@Override
	public UIDefaults getDefaults() {
		UIDefaults table = super.getDefaults();
		// *** Tree
		table.putDefaults(new Object[]{
				"Tree.paintLines", Boolean.FALSE,
				"Tree.rowHeight", 19,
				"Tree.rightChildIndent", 7
		});
		return table;
	}
}
