package com.graann.tree.model;

import com.graann.common.Destroyable;
import com.graann.common.RxUtils;
import com.graann.treeloader.TreeStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class TreeModelController implements Destroyable {
	private static final Logger LOG = LoggerFactory.getLogger(TreeModelController.class);

	private Observable<String> patternObservable;

	private StringFilterFactory stringFilterFactory = new StringFilterFactory();
	private volatile DefaultTreeModel model;
	private TreeFilter treeFilter;
	private StringFilter trigramStringFilter;

	private Subscription filterSubscription;
	private Subscription treeFilterSubscription;
	private Subscription lazyMarkSubscription;

	private BehaviorSubject<List<DefaultMutableTreeNode>> updateObservable = BehaviorSubject.create();

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
				.debounce(200, TimeUnit.MILLISECONDS)
				.switchMap(s -> {
					LOG.debug("new pattern: '"+s+"'");

					pattern = s;
					RxUtils.unsubscribe(treeFilterSubscription);
					RxUtils.unsubscribe(lazyMarkSubscription);

					return trigramStringFilter.appropriateStringObservable(pattern);

				})
				.observeOn(Schedulers.from(SwingUtilities::invokeLater))
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
				.observeOn(Schedulers.from(SwingUtilities::invokeLater))
				.subscribe(t2 -> {
					if (t2 == null || t2._1 == null) {
						model.setRoot(null);
						return;
					}

					DefaultMutableTreeNode defaultMutableTreeNode = t2._1;
					List<DefaultMutableTreeNode> defaultMutableTreeNodes = t2._2;

					LOG.debug(Thread.currentThread().getName());

					setRoot(defaultMutableTreeNode);
					patternLazyMark(defaultMutableTreeNodes);
					updateObservable.onNext(defaultMutableTreeNodes);
				});
	}

	private void setRoot(TreeNode root) {
		model.setRoot(root);
	}

	public Observable<List<DefaultMutableTreeNode>> getUpdateObservable() {
		return updateObservable;
	}

	private void patternLazyMark(List<DefaultMutableTreeNode> mutableTreeNodes) {
		Iterator<DefaultMutableTreeNode> iterator = mutableTreeNodes.iterator();
		patternMark(iterator);
	}



	private void patternMark(Iterator<DefaultMutableTreeNode> iterator) {
		while (iterator.hasNext()) {
			DefaultMutableTreeNode next = iterator.next();
			String s = next.toString();
			String res = "<html>" + s.replace(pattern, "<font color='red'>" + pattern + "</font>") + "</html>";
			next.setUserObject(res);
		}
	}

	@Override
	public void destroy() {
		RxUtils.unsubscribe(filterSubscription);
	}
}
