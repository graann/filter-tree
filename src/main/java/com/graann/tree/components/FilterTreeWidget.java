package com.graann.tree.components;

import com.graann.common.RxUtils;
import com.graann.common.Viewable;
import com.graann.treeloader.TreeLoader;
import net.miginfocom.swing.MigLayout;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

/**
 * @author gromova on 20.09.17.
 */
public class FilterTreeWidget implements Viewable<JComponent> {
	private Subscription loaderSubscriber;
	private BehaviorSubject<String> patternObservable = BehaviorSubject.create();

	private TreeLoader loader;
	private TreeWidgetFactory treeWidgetFactory;

	private JPanel panel;
	private JTextField jTextField = new JTextField();
	private JButton button = new JButton("expand visible");

	void setLoader(TreeLoader loader) {
		this.loader = loader;
	}

	void setTreeWidgetFactory(TreeWidgetFactory treeWidgetFactory) {
		this.treeWidgetFactory = treeWidgetFactory;
	}

	public JComponent getView() {
		return panel;
	}

	void initialize() {

		panel = new JPanel(new MigLayout("fill, wrap 2", "[min!][]", "[min!][]"));

		panel.setPreferredSize(new Dimension(800, 600));
		panel.add(jTextField, "wmin 100");

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

		TreeWidget treeWidget = treeWidgetFactory.create(patternObservable);

		button.addActionListener(e -> treeWidget.expandVisible());

		panel.add(treeWidget.getView(), "grow, span 2");

		loaderSubscriber = loader.loadTreeStructure()
				.subscribeOn(Schedulers.from(SwingUtilities::invokeLater))
				.subscribe(treeWidget::updateStructure);
	}


	@Override
	public void destroy() {
		RxUtils.unsubscribe(loaderSubscriber);
	}
}

