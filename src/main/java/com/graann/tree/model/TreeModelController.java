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

import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.concurrent.TimeUnit;

public class TreeModelController implements Destroyable {
	private static final Logger LOG = LoggerFactory.getLogger(TreeModelController.class);

	private Observable<String> patternObservable;

	private StringFilterFactory stringFilterFactory = new StringFilterFactory();
	private volatile DefaultTreeModel model;
	private TreeFilter treeFilter;
	private StringFilter trigramStringFilter;

	private Subscription filterSubscription;

	private BehaviorSubject<Boolean> updateObservable = BehaviorSubject.create();

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

		filterSubscription = patternObservable
				.distinctUntilChanged()
				.throttleLast(200, TimeUnit.MILLISECONDS)
				.switchMap(s -> {
					LOG.debug("new pattern: '"+s+"'");
					pattern = s;
					return trigramStringFilter.appropriateStringObservable(pattern);

				})
				.switchMap(strings -> {
					if (strings == null) {
						return Observable.just(structure.getRoot());
					}

					if (strings.isEmpty()) {
						return Observable.just(null);
					}

					return treeFilter.rootObservable(structure, strings);
				})
				.distinctUntilChanged()
				.observeOn(Schedulers.from(SwingUtilities::invokeLater))
				.subscribe(root -> {
					model.setRoot(root);

					if (root instanceof RootTreeNode) {
						RootTreeNode rootTreeNode = (RootTreeNode) root;
						LOG.debug(pattern+": "+rootTreeNode.getSelectedNodes().size());
						System.out.println(pattern+": "+rootTreeNode.getSelectedNodes().size());
						for (DefaultMutableTreeNode next : rootTreeNode.getSelectedNodes()) {
							String s = next.toString();
							String res = "<html>" + s.replace(pattern, "<font color='red'>" + pattern + "</font>") + "</html>";
							next.setUserObject(res);
						}
						updateObservable.onNext(true);
					} else {
						updateObservable.onNext(false);
					}


				});
	}

	public Observable<Boolean> getUpdateObservable() {
		return updateObservable;
	}


	@Override
	public void destroy() {
		RxUtils.unsubscribe(filterSubscription);
	}
}
