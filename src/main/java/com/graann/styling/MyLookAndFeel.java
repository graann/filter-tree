package com.graann.styling;

import com.pagosoft.plaf.PgsLookAndFeel;

import javax.swing.*;

public class MyLookAndFeel extends PgsLookAndFeel {
	@Override
	protected void initClassDefaults(UIDefaults table) {
		super.initClassDefaults(table);
		table.put("TreeUI", MyTreeUI.class.getName());
	}

	@Override
	public UIDefaults getDefaults() {
		UIDefaults table = super.getDefaults();
		table.putDefaults(new Object[]{
				// *** Tree
				"Tree.paintLines", Boolean.FALSE,
				"Tree.expandedIcon", FontIcon.builder().symbol(IconFontSymbols.ARROW_DOWN.getString())
				.color(ColorScheme.DEFAULT_ICON).build(),
				"Tree.collapsedIcon", FontIcon.builder().symbol(IconFontSymbols.ARROW_RIGHT.getString())
				.color(ColorScheme.DEFAULT_ICON).build(),
				"Tree.rowHeight", Integer.valueOf(22),
				"Tree.totalChildIndent", Integer.valueOf(15),
				"Tree.rightChildIndent", Integer.valueOf(10),
				"Tree.leftChildIndent", Integer.valueOf(15),
				"Tree.leafIcon", FontIcon.builder().symbol(IconFontSymbols.LEAF.getString())
				.color(ColorScheme.LEAF_ICON).build(),
				"Tree.closedIcon", FontIcon.builder().symbol(IconFontSymbols.FOLDER.getString())
				.color(ColorScheme.FOLDER_ICON).build(),
				"Tree.openIcon", FontIcon.builder().symbol(IconFontSymbols.OPEN_FOLDER.getString())
				.color(ColorScheme.FOLDER_ICON).build(),

				// *** Label
				"Label.foreground", ColorScheme.MAINT
		});
		return table;
	}


}
