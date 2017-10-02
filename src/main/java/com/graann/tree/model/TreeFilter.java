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

import javax.swing.SwingUtilities;
import javax.swing.tree.TreeNode;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class TreeFilter implements Destroyable {
	private static final Logger LOG = LoggerFactory.getLogger(TreeFilter.class);

	private Observable<String> patternObservable;

	private StringFilterFactory stringFilterFactory = new StringFilterFactory();
	private TreeNodeFilter treeNodeFilter;
	private StringFilter trigramStringFilter;

	private Subscription filterSubscription;

	private BehaviorSubject<Tuple2<String, TreeNode>> filterObservable = BehaviorSubject.create();

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
		trigramStringFilter = stringFilterFactory.create(structure.getStrings());

		RxUtils.unsubscribe(filterSubscription);

		filterSubscription = patternObservable
				.distinctUntilChanged()
				.throttleLast(200, TimeUnit.MILLISECONDS)
				.switchMap(s -> {
					if(s == null) {
						return Observable.just(new Filtered(s, null));
					}
					return trigramStringFilter.appropriateStringObservable(s)
							.map(strings -> new Filtered(s, strings));
				})
				.switchMap(filtered -> {
					if (filtered.strings == null) {
						return Observable.just(Tuples.t(filtered.pattern, structure.getRoot()));
					}

					if (filtered.strings.isEmpty()) {
						return Observable.just(Tuples.t(filtered.pattern, ((TreeNode) null)));
					}

					return treeNodeFilter.rootObservable(structure, filtered.strings)
							.map(treeNode -> Tuples.t(filtered.pattern, treeNode));
				})
				.distinctUntilChanged()
				.observeOn(Schedulers.from(SwingUtilities::invokeLater))
				.subscribe(t ->	filterObservable.onNext(t));
	}


	public Observable<Tuple2<String, TreeNode>> filteredStateObservable() {
		return filterObservable;
	}


	@Override
	public void destroy() {
		RxUtils.unsubscribe(filterSubscription);
	}

	public class Filtered {
		String pattern;
		Set<String> strings;

		public Filtered(String pattern, Set<String> strings) {
			this.pattern = pattern;
			this.strings = strings;
		}
	}
}
