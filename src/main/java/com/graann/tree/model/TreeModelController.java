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

public class TreeModelController implements Destroyable {
	private static final Logger LOG = LoggerFactory.getLogger(TreeModelController.class);

	private Observable<String> patternObservable;

	private StringFilterFactory stringFilterFactory = new StringFilterFactory();
	private TreeFilter treeFilter;
	private StringFilter trigramStringFilter;

	private Subscription filterSubscription;

	private BehaviorSubject<Tuple2<String, TreeNode>> updateObservable = BehaviorSubject.create();

	public void setStringFilterFactory(StringFilterFactory stringFilterFactory) {
		this.stringFilterFactory = stringFilterFactory;
	}

	public void setTreeFilter(TreeFilter treeFilter) {
		this.treeFilter = treeFilter;
	}

	public void setPatternObservable(Observable<String> patternObservable) {
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

					return treeFilter.rootObservable(structure, filtered.strings)
							.map(treeNode -> Tuples.t(filtered.pattern, treeNode));
				})
				.distinctUntilChanged()
				.observeOn(Schedulers.from(SwingUtilities::invokeLater))
				.subscribe(t ->	updateObservable.onNext(t));
	}


	public Observable<Tuple2<String, TreeNode>> getUpdateObservable() {
		return updateObservable;
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
