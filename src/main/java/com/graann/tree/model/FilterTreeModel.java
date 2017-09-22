package com.graann.tree.model;

import rx.Observable;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * @author gromova on 21.09.17.
 */
public class FilterTreeModel implements TreeModel {
	private Observable<String> filterObservable;
	private TreeModel treeModel;

	public void setFilterObservable(Observable<String> filterObservable) {
		this.filterObservable = filterObservable;
	}

	public FilterTreeModel(TreeModel treeModel) {
		this.treeModel = treeModel;
	}

	public void initialize(){
		//filterObservable.subscribe(s -> )
	}

	@Override
	public Object getRoot() {
		return treeModel.getRoot();
	}

	@Override
	public Object getChild(Object parent, int index) {
		return treeModel.getChild(parent, index);
	}

	@Override
	public int getChildCount(Object parent) {
		return treeModel.getChildCount(parent);
	}

	@Override
	public boolean isLeaf(Object node) {
		return treeModel.isLeaf(node);
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		treeModel.valueForPathChanged(path, newValue);
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		return treeModel.getIndexOfChild(parent, child);
	}

	@Override
	public void addTreeModelListener(TreeModelListener l) {
		treeModel.addTreeModelListener(l);
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l) {
		treeModel.removeTreeModelListener(l);
	}


}
