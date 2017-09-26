package com.graann.tree.components;

import javax.swing.*;
import javax.swing.tree.TreeModel;

/**
 * @author gromova on 26.09.17.
 */
public class CustomTree extends JTree {

	public CustomTree(TreeModel newModel) {
		super(newModel);
		setCellRenderer(new FilterTreeCellRenderer());
		setExpandsSelectedPaths(true);
	}

}
