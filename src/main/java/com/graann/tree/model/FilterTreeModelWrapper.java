package com.graann.tree.model;

import rx.Observable;

import javax.swing.tree.TreeModel;

public class FilterTreeModelWrapper {

    public TreeModel wrap(TreeModel model, Observable<String> filterKeyObservable) {
        FilterTreeModel wrapper = new FilterTreeModel(model);
        wrapper.setFilterKeyObservable(filterKeyObservable);
        wrapper.initialize();
        return wrapper;
    }
}
