package com.graann.tree.components;

import com.graann.tree.model.TreeModelController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JTree;
import javax.swing.tree.TreeModel;

/**
 * @author gromova on 26.09.17.
 */
public class CustomTree extends JTree {
	private static final Logger LOG = LoggerFactory.getLogger(TreeModelController.class);
	private boolean expandAvailable = true;

	public CustomTree(TreeModel newModel) {
		super(newModel);
		setCellRenderer(new FilterTreeCellRenderer());
		setExpandsSelectedPaths(true);
	}

	public void setExpandAvailable(boolean expandAvailable) {
		LOG.info("expandAvailable: "+expandAvailable);
		this.expandAvailable = expandAvailable;
	}

	public boolean isExpandAvailable() {
		return expandAvailable;
	}

	public void expandNodes(int startingIndex, int stopIndex) {
		for (int i = startingIndex; i <= stopIndex && expandAvailable; i++) {
			expandRow(i);
		}
	}

	@Override
	public void expandRow(int row) {
		if (expandAvailable) {
			super.expandRow(row);
		}
	}
}
