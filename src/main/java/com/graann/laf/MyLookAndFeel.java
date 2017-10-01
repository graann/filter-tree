package com.graann.laf;

import com.pagosoft.plaf.PgsLookAndFeel;

import javax.swing.*;
import java.awt.*;

public class MyLookAndFeel extends PgsLookAndFeel {
	@Override
	protected void initClassDefaults(UIDefaults table) {
		super.initClassDefaults(table);
		table.put("TreeUI", MyTreeUI.class.getName());
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
				"Tree.totalChildIndent", 15,
				"Tree.rightChildIndent", 10,
				"Tree.leftChildIndent", 15,
				"Tree.leafIcon", FontIcon.builder().symbol(IconFontSymbols.LEAF.getString())
				.color(new Color(0x666666)).build(),
				"Tree.closedIcon", FontIcon.builder().symbol(IconFontSymbols.FOLDER.getString())
				.color(new Color(0x666666)).build(),
				"Tree.openIcon", FontIcon.builder().symbol(IconFontSymbols.OPEN_FOLDER.getString())
				.color(new Color(0x666666)).build(),
		});
		return table;
	}


}
