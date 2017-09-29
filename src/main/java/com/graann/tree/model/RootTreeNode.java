package com.graann.tree.model;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Collections;
import java.util.Set;

/**
 * @author gromova on 27.09.17.
 */
public class RootTreeNode extends DefaultMutableTreeNode {
	private Set<DefaultMutableTreeNode> selectedNodes = Collections.emptySet();

	public void setSelectedNodes(Set<DefaultMutableTreeNode> selectedNodes) {
		this.selectedNodes = selectedNodes;
	}

	public Set<DefaultMutableTreeNode> getSelectedNodes() {
		return selectedNodes;
	}

	public RootTreeNode(Object userObject) {
		super(userObject);
	}
}
