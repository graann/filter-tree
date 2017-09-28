package com.graann.laf;

import com.alee.laf.WebLookAndFeel;

import javax.swing.*;

public class CustomWebLookAndFeel extends WebLookAndFeel {
	@Override
	protected void initClassDefaults(UIDefaults table) {
		super.initClassDefaults(table);
		table.put("TreeUI", CustomTreeUI.class.getName());
	}
}
