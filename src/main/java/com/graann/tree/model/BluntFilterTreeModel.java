package com.graann.tree.model;

import rx.Observable;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * @author gromova on 21.09.17.
 */
public class BluntFilterTreeModel implements TreeModel {
	private Observable<String> filterObservable;
	private TreeModel treeModel;
	private String filter = "";

	public void setFilterObservable(Observable<String> filterObservable) {
		this.filterObservable = filterObservable;
	}

	public BluntFilterTreeModel(TreeModel treeModel) {
		this.treeModel = treeModel;
	}

	public void initialize(){
		filterObservable.subscribe(s -> filter = s);
	}

	private boolean recursiveMatch(final Object node, final String filter) {
		boolean matches = node.toString().contains(filter);

		int childCount = treeModel.getChildCount(node);
		for (int i = 0; i < childCount; i++) {
			Object child = treeModel.getChild(node, i);
			matches |= recursiveMatch(child, filter);
		}

		return matches;
	}

	@Override
	public Object getRoot() {
		return treeModel.getRoot();
	}

	@Override
	public Object getChild(final Object parent, final int index) {
		int count = 0;
		int childCount = treeModel.getChildCount(parent);
		for (int i = 0; i < childCount; i++) {
			Object child = treeModel.getChild(parent, i);
			if (recursiveMatch(child, filter)) {
				if (count == index) {
					return child;
				}
				count++;
			}
		}
		return null;
	}

	@Override
	public int getChildCount(final Object parent) {
		int count = 0;
		int childCount = treeModel.getChildCount(parent);
		for (int i = 0; i < childCount; i++) {
			Object child = treeModel.getChild(parent, i);
			if (recursiveMatch(child, filter)) {
				count++;
			}
		}
		return count;
	}

	@Override
	public boolean isLeaf(final Object node) {
		return treeModel.isLeaf(node);
	}

	@Override
	public void valueForPathChanged(final TreePath path, final Object newValue) {
		treeModel.valueForPathChanged(path, newValue);
	}

	@Override
	public int getIndexOfChild(final Object parent, final Object childToFind) {
		int childCount = treeModel.getChildCount(parent);
		for (int i = 0; i < childCount; i++) {
			Object child = treeModel.getChild(parent, i);
			if (recursiveMatch(child, filter)) {
				if (childToFind.equals(child)) {
					return i;
				}
			}
		}
		return -1;
	}

	@Override
	public void addTreeModelListener(final TreeModelListener l) {
		treeModel.addTreeModelListener(l);
	}

	@Override
	public void removeTreeModelListener(final TreeModelListener l) {
		treeModel.removeTreeModelListener(l);
	}

}
