package com.graann.tree.components;

import com.graann.common.RxUtils;
import com.graann.common.Viewable;
import com.graann.tree.model.TreeModelController;
import com.graann.tree.model.TreeModelControllerFactory;
import com.graann.treeloader.TreeStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.concurrent.TimeUnit;

/**
 * @author gromova on 22.09.17.
 */
public class TreeWidget implements Viewable<JComponent>, AdjustmentListener {
	private static final Logger LOG = LoggerFactory.getLogger(TreeWidget.class);

	private Observable<String> patternObservable;
	private Subscription lockSubscriber;
	private Subscription expandSubscription;

	private TreeModelControllerFactory modelControllerFactory;

	private TreeModelController treeModelController;

	private CustomTree tree;
	private JScrollPane scrollPane;
	private DefaultTreeModel model;

	private BehaviorSubject<Boolean> verticalScrollObservable = BehaviorSubject.create();

	void setModelControllerFactory(TreeModelControllerFactory modelControllerFactory) {
		this.modelControllerFactory = modelControllerFactory;
	}

	void setPatternObservable(Observable<String> patternObservable) {
		this.patternObservable = patternObservable;
	}

	@Override
	public JComponent getView() {
		return scrollPane;
	}

	void initialize() {
		model = new DefaultTreeModel(null);

		tree = new CustomTree(model);

		scrollPane = new JScrollPane(tree);
		treeModelController = modelControllerFactory.create(model, patternObservable);

		scrollPane.getVerticalScrollBar().addAdjustmentListener(e -> verticalScrollObservable.onNext(true));

		//TODO
		patternObservable.subscribe(s -> {
			LOG.debug("new pattern: '"+s+"'");
			lock = true;
			tree.setExpandAvailable(false);
			RxUtils.unsubscribe(expandSubscription);
		});

		//TODO
		treeModelController
				.getUpdateObservable()
				.subscribeOn(Schedulers.from(SwingUtilities::invokeLater))
				.subscribe(aVoid -> {
					LOG.debug("treeModelController subscription");
					lock = false;
					tree.setExpandAvailable(true);
					expandVisible();
					updateExpandSubscription();
			});
	}

	private void updateExpandSubscription() {
		expandSubscription = verticalScrollObservable
				.throttleLast(200, TimeUnit.MILLISECONDS)
				.subscribe(aBoolean -> {
					LOG.debug("expandSubscription");
					expandVisible();
				});
	}

	@Override
	public void destroy() {
		treeModelController.destroy();
	}

	void updateStructure(TreeStructure structure) {
		model.setRoot(structure.getRoot());
		treeModelController.updateStructure(structure);
	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		expandVisible();
	}

	private boolean lock;
	private void expandVisible() {
		if (lock) {
			return;
		}
		RxUtils.unsubscribe(lockSubscriber);
		lock = true;

		final Rectangle visibleRectangle = scrollPane.getViewport().getViewRect();
		final int firstRow = tree.getClosestRowForLocation(visibleRectangle.x, visibleRectangle.y);
		int lastRow = tree.getClosestRowForLocation(visibleRectangle.x, visibleRectangle.y + visibleRectangle.height);
		tree.expandNodes(firstRow, lastRow);

		lockSubscriber = Observable.interval(50, TimeUnit.MILLISECONDS).subscribe(aLong -> {
			if (lock) {
				lock = false;
				RxUtils.unsubscribe(lockSubscriber);
			}
		});
	}


}
