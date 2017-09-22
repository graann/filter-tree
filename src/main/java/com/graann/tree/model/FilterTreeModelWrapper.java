package com.graann.tree.model;

import rx.Observable;

import javax.swing.tree.TreeModel;

public class FilterTreeModelWrapper {

	public TreeModel wrap(Observable<TreeModel> modelObservable, TreeModel model) {
		FilterTreeModel wrapper = new FilterTreeModel(model);
		wrapper.setModelObservable(modelObservable);
		return wrapper;
	}
}
