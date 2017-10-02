package com.graann.tree.model;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author gromova on 27.09.17.
 */
public class RootTreeNode extends DefaultMutableTreeNode {
	private List<DefaultMutableTreeNode> selectedNodes = Collections.emptyList();
	private AtomicInteger filteredCount;

	public void setSelectedNodes(List<DefaultMutableTreeNode> selectedNodes) {
		this.selectedNodes = selectedNodes;
	}

	public List<DefaultMutableTreeNode> getSelectedNodes() {
		return selectedNodes;
	}

	public void setFilteredCount(AtomicInteger filteredCount) {
		this.filteredCount = filteredCount;
	}

	public int getFilteredCount() {
		return filteredCount.get();
	}

	public RootTreeNode(Object userObject) {
		super(userObject);
	}
}
