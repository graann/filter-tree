package com.graann.tree.filter;

import com.graann.common.Destroyable;
import com.graann.common.RxUtils;
import com.graann.treeloader.TreeStructure;
import org.reactfx.util.Tuples;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

import java.util.concurrent.TimeUnit;

public class TreeFilter implements Destroyable {
	private Observable<TreeStructure> structureObservable;
	private Observable<String> patternObservable;

	private StringFilterFactory stringFilterFactory = new StringFilterFactory();
	private TreeNodeFilter treeNodeFilter;
	private StringFilter trigramStringFilter;

	private Subscription subscription;
	private Subscription filterSubscription;

	private final BehaviorSubject<FilteredState> filterObservable = BehaviorSubject.create();

	void setStructureObservable(Observable<TreeStructure> structureObservable) {
		this.structureObservable = structureObservable;
	}

	void setStringFilterFactory(StringFilterFactory stringFilterFactory) {
		this.stringFilterFactory = stringFilterFactory;
	}

	void setTreeNodeFilter(TreeNodeFilter treeNodeFilter) {
		this.treeNodeFilter = treeNodeFilter;
	}

	void setPatternObservable(Observable<String> patternObservable) {
		this.patternObservable = patternObservable;
	}

	void init() {
		subscription = structureObservable
				.subscribeOn(Schedulers.computation())
				.subscribe(structure -> {

					filterObservable.onNext(new FilteredState(structure.getRoot(), structure.getCount()));

					trigramStringFilter = stringFilterFactory.create(structure.getStrings());
					clear();

					filterSubscription = patternObservable
							.debounce(200, TimeUnit.MILLISECONDS)
							.switchMap(s -> {
								if (s == null || s.isEmpty()) {
									return Observable.just(null);
								}
								return trigramStringFilter.appropriateStringObservable(s).map(strings -> Tuples.t(s, strings));
							})
							.switchMap(t2 -> {
								if (t2 == null) {
									return Observable.just(new FilteredState(structure.getRoot(), structure.getCount()));
								}

								if (t2._2 ==null || t2._2.isEmpty()) {
									return Observable.just(new FilteredState(t2._1, structure.getCount()));
								}

								return treeNodeFilter.rootObservable(structure, t2._2)
										.map(tuple3 -> {
											if (tuple3 == null) {
												return new FilteredState(t2._1, structure.getCount());
											}


											return new FilteredState(t2._1, tuple3._1, tuple3._2, structure.getCount(), tuple3._3);
										});
							})
							.subscribe(filterObservable::onNext);
				});

	}

	public Observable<FilteredState> filteredStateObservable() {
		return filterObservable;
	}

	@Override
	public void destroy() {
		RxUtils.unsubscribe(subscription);
		clear();
	}

	private void clear() {
		RxUtils.unsubscribe(filterSubscription);
		if (trigramStringFilter != null) {
			trigramStringFilter.destroy();
		}
	}
}
