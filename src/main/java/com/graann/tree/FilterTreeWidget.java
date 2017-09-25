package com.graann.tree;

import com.graann.common.RxUtils;
import com.graann.common.Viewable;
import com.graann.tree.components.TreeWidgetFactory;
import com.graann.tree.model.filter.StringFilter;
import com.graann.tree.model.filter.StringFilterFactory;
import com.graann.tree.model.filter.TreeFilter;
import com.graann.treeloader.TreeLoader;
import net.miginfocom.swing.MigLayout;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.Dimension;
import java.util.List;

/**
 * @author gromova on 20.09.17.
 */
public class FilterTreeWidget implements Viewable<JComponent> {
	private Subscription loaderSubscriber;
	private Subscription filterSubscription;

	private StringFilterFactory stringFilterFactory = new StringFilterFactory();
	private TreeFilter treeFilter;
	private TreeLoader loader;
	private TreeWidgetFactory treeWidgetFactory;

	private JPanel panel;

	private JTextField jTextField = new JTextField();
	private JButton button = new JButton("expand");

	public void setTreeFilter(TreeFilter treeFilter) {
		this.treeFilter = treeFilter;
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

		Viewable<JComponent> treeWidget = treeWidgetFactory.create(model);
		panel.add(treeWidget.getView(), "grow, span 2");

		loaderSubscriber = loader.loadTreeStructure()
				.subscribeOn(Schedulers.from(SwingUtilities::invokeLater))
				.subscribe(structure -> {

					StringFilter trigramStringFilter = stringFilterFactory.create(structure.getStrings());

					filterSubscription = patternObservable
							.switchMap(trigramStringFilter::appropriateStringObservable)
							.subscribeOn(Schedulers.from(SwingUtilities::invokeLater))
							.subscribe(strings -> {
								if (strings == null) {
									model.setRoot(structure.getRoot());
									return;
								}

								if (strings.isEmpty()) {
									model.setRoot(null);
									return;
								}

								treeFilter.rootObservable(structure, strings)
										.subscribeOn(Schedulers.from(SwingUtilities::invokeLater))
										.subscribe(t2 -> {
											if (t2 == null) {
												model.setRoot(null);
												return;
											}

											DefaultMutableTreeNode defaultMutableTreeNode = t2._1;
											List<DefaultMutableTreeNode> defaultMutableTreeNodes = t2._2;

											model.setRoot(defaultMutableTreeNode);
										});
							});

					model.setRoot(structure.getRoot());
				});
	}


	@Override
	public void destroy() {
		RxUtils.unsubscribe(loaderSubscriber);
		RxUtils.unsubscribe(filterSubscription);
	}
}

