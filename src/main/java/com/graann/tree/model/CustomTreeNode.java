package com.graann.tree.model;

import javax.swing.tree.DefaultMutableTreeNode;

public class CustomTreeNode extends DefaultMutableTreeNode {
	public String htmlText;
	private boolean marked = false;

	public boolean markSelection() {
		if (!marked) {
			setUserObject(htmlText);
			marked = true;
		}
		return marked;
	}

	public CustomTreeNode(String text, String htmlText) {
		super(text);
		this.htmlText = htmlText;
	}
}
