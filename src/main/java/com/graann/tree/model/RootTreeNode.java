package com.graann.tree.model;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Collections;
import java.util.List;

/**
 * @author gromova on 27.09.17.
 */
public class RootTreeNode extends DefaultMutableTreeNode {
	private List<DefaultMutableTreeNode> selectedNodes = Collections.emptyList();

	public void setSelectedNodes(List<DefaultMutableTreeNode> selectedNodes) {
		this.selectedNodes = selectedNodes;
	}

	public List<DefaultMutableTreeNode> getSelectedNodes() {
		return selectedNodes;
	}

	public RootTreeNode(Object userObject) {
		super(userObject);
	}
}
