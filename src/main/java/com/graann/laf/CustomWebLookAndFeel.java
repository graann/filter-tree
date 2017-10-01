package com.graann.laf;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;

public class CustomWebLookAndFeel extends MetalLookAndFeel {
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
				"Tree.expandedIcon", FontIcon.builder().symbol(IconFontSymbols.ARROW_DOWN.getString())
				.color(new Color(0x666666)).build(),
				"Tree.collapsedIcon", FontIcon.builder().symbol(IconFontSymbols.ARROW_RIGHT.getString())
				.color(new Color(0x666666)).build(),
				"Tree.rowHeight", 25,
				"Tree.rightChildIndent", 10,
				"Tree.leftChildIndent", 10
		});
		return table;
	}


}
