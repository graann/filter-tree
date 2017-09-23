package com.graann.tree.components;

import com.graann.common.Viewable;

import javax.swing.*;
import javax.swing.tree.TreeModel;

public class TreeWidgetFactory {
	public Viewable<JComponent> create(TreeModel model) {
		TreeWidget widget = new TreeWidget();
		widget.setModel(model);
		widget.initialize();
		return widget;
	}

}
