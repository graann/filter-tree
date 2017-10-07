package com.graann.tree.ui;

import com.graann.tree.filter.TreeFilter;
import com.graann.tree.filter.TreeFilterFactory;
import rx.Observable;
import rx.subjects.BehaviorSubject;

import java.awt.*;
import java.util.function.BiConsumer;

class CustomTreeFactory {
	private TreeFilterFactory treeFilterFactory;

	void setTreeFilterFactory(TreeFilterFactory treeFilterFactory) {
		this.treeFilterFactory = treeFilterFactory;
	}

	CustomTree createTree(Observable<String> patternObservable,
						  BehaviorSubject<Rectangle> viewportAreaObservable,
						  BiConsumer<Integer, Integer> counterConsumer) {

		CustomTree tree = new CustomTree();
		TreeFilter treeFilter = treeFilterFactory.create(patternObservable);
		tree.setViewportAreaObservable(viewportAreaObservable);
		tree.setCounterConsumer(counterConsumer);
		tree.setFilterObservable(treeFilter.filteredStateObservable());
		tree.setDestroyableTask(treeFilter::destroy);
		tree.init();
		return tree;
	}
}
