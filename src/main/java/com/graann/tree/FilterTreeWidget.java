package com.graann.tree;

import com.graann.common.Viewable;
import com.graann.tree.model.filter.Filter;
import com.graann.tree.model.filter.FilterFactory;
import com.graann.tree.components.TreeWidgetFactory;
import com.graann.treeloader.TreeLoader;
import net.miginfocom.swing.MigLayout;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.awt.*;

/**
 * @author gromova on 20.09.17.
 */
public class FilterTreeWidget implements Viewable<JComponent> {
	private Subscription subscribe;

	private FilterFactory filterFactory;
	private TreeLoader loader;
	private TreeWidgetFactory treeWidgetFactory;

	private JPanel panel;

	private JTextField jTextField = new JTextField();
	private JButton button = new JButton("expand");


	public void setFilterFactory(FilterFactory filterFactory) {
		this.filterFactory = filterFactory;
	}

	public void setLoader(TreeLoader loader) {
		this.loader = loader;
	}

	public void setTreeWidgetFactory(TreeWidgetFactory treeWidgetFactory) {
		this.treeWidgetFactory = treeWidgetFactory;
	}

	public JComponent getView() {
		return panel;
	}

	public void initialize() {

		panel = new JPanel(new MigLayout("fill, wrap 2", "[min!][]", "[min!][]"));

		panel.setPreferredSize(new Dimension(800, 600));
		panel.add(jTextField, "wmin 100");

		BehaviorSubject<String> patternObservable = BehaviorSubject.create();

		jTextField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				update();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				update();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				update();
			}

			private void update() {
				String text = jTextField.getText();
				patternObservable.onNext(text);
			}
		});

		panel.add(button);

		DefaultTreeModel model = new DefaultTreeModel(null);
		Filter filter = filterFactory.create(patternObservable);

		Viewable<JComponent> treeWidget = treeWidgetFactory.create(model);
		panel.add(treeWidget.getView(), "grow, span 2");

		subscribe = loader.loadTreeStructure()
				.subscribeOn(Schedulers.from(SwingUtilities::invokeLater))
				.subscribe(structure -> {
					model.setRoot(structure.getRoot());
					Observable<TreeNode> treeNodeObservable = filter.rootObservable(structure);
					treeNodeObservable
							.subscribeOn(Schedulers.from(SwingUtilities::invokeLater))
							.subscribe(model::setRoot);
				});
	}

	@Override
	public void destroy() {
		if (subscribe != null && !subscribe.isUnsubscribed()) {
			subscribe.unsubscribe();
		}
	}
}

