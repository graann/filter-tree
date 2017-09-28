package com.graann.tree.components;

import com.graann.common.RxUtils;
import com.graann.common.Viewable;
import com.graann.treeloader.TreeLoader;
import net.miginfocom.swing.MigLayout;
import rx.Subscription;
import rx.schedulers.Schedulers;

import javax.swing.*;
import java.awt.*;

/**
 * @author gromova on 20.09.17.
 */
public class FilterTreeWidget implements Viewable<JComponent> {
	private Subscription loaderSubscriber;

	private TreeLoader loader;
	private TreeWidgetFactory treeWidgetFactory;

	private JPanel panel;
	private JButton button = new JButton("bla");

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

		panel = new JPanel(new MigLayout("fill, flowy", "[min!][]", "[min!][]"));

		panel.setPreferredSize(new Dimension(800, 600));

		TreeWidget treeWidget = treeWidgetFactory.create();

		panel.add(button);
		panel.add(treeWidget.getView(), "grow, span 2");

		loaderSubscriber = loader.loadTreeStructure()
				.observeOn(Schedulers.from(SwingUtilities::invokeLater))
				.subscribe(treeWidget::updateStructure);
	}


	@Override
	public void destroy() {
		RxUtils.unsubscribe(loaderSubscriber);
	}
}

