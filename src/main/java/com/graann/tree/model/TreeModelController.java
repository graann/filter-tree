package com.graann.tree.model;

import com.graann.common.Destroyable;
import com.graann.common.RxUtils;
import com.graann.treeloader.TreeStructure;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class TreeModelController implements Destroyable {
	private Observable<String> patternObservable;

	private StringFilterFactory stringFilterFactory = new StringFilterFactory();
	private DefaultTreeModel model;
	private TreeFilter treeFilter;
	private StringFilter trigramStringFilter;

	private Subscription filterSubscription;
	private Subscription treeFilterSubscription;
	private Subscription lazyMarkSubscription;

	private TreeStructure structure;
	private String pattern;

	public void setStringFilterFactory(StringFilterFactory stringFilterFactory) {
		this.stringFilterFactory = stringFilterFactory;
	}

	public void setModel(DefaultTreeModel model) {
		this.model = model;
	}

	public void setTreeFilter(TreeFilter treeFilter) {
		this.treeFilter = treeFilter;
	}

	public void setPatternObservable(Observable<String> patternObservable) {
		this.patternObservable = patternObservable;
	}

	public void updateStructure(TreeStructure structure) {
		this.structure = structure;
		trigramStringFilter = stringFilterFactory.create(structure.getStrings());

		RxUtils.unsubscribe(filterSubscription);
		RxUtils.unsubscribe(lazyMarkSubscription);
		RxUtils.unsubscribe(treeFilterSubscription);

		filterSubscription = patternObservable
				.distinctUntilChanged()
				.throttleWithTimeout(70, TimeUnit.MILLISECONDS)
				.switchMap(s -> {
					pattern = s;
					return trigramStringFilter.appropriateStringObservable(pattern);

				})
				.subscribeOn(Schedulers.from(SwingUtilities::invokeLater))
				.subscribe(this::filteredStrings);
	}


	private void filteredStrings(Set<String> strings) {
		RxUtils.unsubscribe(lazyMarkSubscription);
		RxUtils.unsubscribe(treeFilterSubscription);

		if (strings == null) {
			model.setRoot(structure.getRoot());
			return;
		}

		if (strings.isEmpty()) {
			model.setRoot(null);
			return;
		}

		treeFilterSubscription = treeFilter.rootObservable(structure, strings)
				.subscribeOn(Schedulers.from(SwingUtilities::invokeLater))
				.subscribe(t2 -> {
					if (t2 == null) {
						model.setRoot(null);
						return;
					}

					DefaultMutableTreeNode defaultMutableTreeNode = t2._1;
					List<DefaultMutableTreeNode> defaultMutableTreeNodes = t2._2;

					model.setRoot(defaultMutableTreeNode);
					patternLazyMark(defaultMutableTreeNodes);
				});
	}

	private void patternLazyMark(List<DefaultMutableTreeNode> mutableTreeNodes) {
		Iterator<DefaultMutableTreeNode> iterator = mutableTreeNodes.iterator();
		RxUtils.unsubscribe(lazyMarkSubscription);
		lazyMarkSubscription = Observable.interval(10, TimeUnit.MILLISECONDS, Schedulers.from(SwingUtilities::invokeLater))
				.subscribeOn(Schedulers.from(SwingUtilities::invokeLater))
				.subscribe(aLong -> {
					int i = 100;
					while (i > 0 && iterator.hasNext()) {
						DefaultMutableTreeNode next = iterator.next();
						String s = next.toString();
						String res = "<html>" + s.replace(pattern, "<font color='red'>" + pattern + "</font>") + "</html>";
						next.setUserObject(res);
						i--;
					}

					if (!iterator.hasNext()) {
						RxUtils.unsubscribe(lazyMarkSubscription);
					}
				});

	}

	@Override
	public void destroy() {
		RxUtils.unsubscribe(filterSubscription);
	}
}