package com.graann.tree.model;

import com.graann.common.Destroyable;
import com.graann.common.RxUtils;
import com.graann.treeloader.TreeStructure;
import org.reactfx.util.Tuple2;
import org.reactfx.util.Tuples;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import java.util.concurrent.TimeUnit;

public class TreeFilter implements Destroyable {
	private Observable<String> patternObservable;

	private StringFilterFactory stringFilterFactory = new StringFilterFactory();
	private TreeNodeFilter treeNodeFilter;
	private StringFilter trigramStringFilter;

	private Subscription filterSubscription;

	private final BehaviorSubject<Tuple2<String, TreeNode>> filterObservable = BehaviorSubject.create();

	void setStringFilterFactory(StringFilterFactory stringFilterFactory) {
		this.stringFilterFactory = stringFilterFactory;
	}

	void setTreeNodeFilter(TreeNodeFilter treeNodeFilter) {
		this.treeNodeFilter = treeNodeFilter;
	}

	void setPatternObservable(Observable<String> patternObservable) {
		this.patternObservable = patternObservable;
	}

	public void updateStructure(TreeStructure structure) {
		clear();

		trigramStringFilter = stringFilterFactory.create(structure.getStrings());

		filterSubscription = patternObservable
				.distinctUntilChanged()
				.throttleLast(200, TimeUnit.MILLISECONDS)
				.startWith("")
				.switchMap(s -> {
					if(s == null || s.isEmpty()) {
						return Observable.just(null);
					}
					return trigramStringFilter.appropriateStringObservable(s)
							.map(strings -> Tuples.t(s, strings));
				})
				.switchMap(tuple2 -> {
					if (tuple2 == null || tuple2._2 == null) {
						return Observable.just(Tuples.t("", structure.getRoot()));
					}

					if (tuple2._2.isEmpty()) {
						return Observable.just(Tuples.t(tuple2._1, ((TreeNode) null)));
					}

					return treeNodeFilter.rootObservable(structure, tuple2._2)
							.map(treeNode -> Tuples.t(tuple2._1, treeNode));
				})
				.distinctUntilChanged()
				.observeOn(Schedulers.from(SwingUtilities::invokeLater))
				.subscribe(filterObservable::onNext);
	}


	public Observable<Tuple2<String, TreeNode>> filteredStateObservable() {
		return filterObservable;
	}


	@Override
	public void destroy() {
		clear();
	}

	private void clear() {
		RxUtils.unsubscribe(filterSubscription);
		if(trigramStringFilter != null) {
			trigramStringFilter.destroy();
		}
	}
}
