package com.graann.tree.model;

import rx.Observable;

import javax.swing.tree.TreeModel;

public class FilterTreeModelWrapper {

    public TreeModel wrap(TreeModel model, Observable<String> filterObservable) {
        FilterTreeModel wrapper = new FilterTreeModel(model);
        wrapper.setFilterObservable(filterObservable);
        wrapper.initialize();
        return wrapper;
    }
}
