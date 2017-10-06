package com.graann.tree.filter;

import com.graann.common.Reference;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Collections;
import java.util.List;

/**
 * @author gromova on 27.09.17.
 */
public class RootTreeNode extends DefaultMutableTreeNode {
	private List<DefaultMutableTreeNode> selectedNodes = Collections.emptyList();
	private Reference<Integer> filteredCount;

	public void setSelectedNodes(List<DefaultMutableTreeNode> selectedNodes) {
		this.selectedNodes = selectedNodes;
	}

	public List<DefaultMutableTreeNode> getSelectedNodes() {
		return selectedNodes;
	}

	public void setFilteredCount(Reference<Integer> filteredCount) {
		this.filteredCount = filteredCount;
	}

	public int getFilteredCount() {
		return filteredCount.getValue();
	}

	public RootTreeNode(Object userObject) {
		super(userObject);
	}
}
