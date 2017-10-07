package com.graann.tree.ui;

import com.graann.common.Viewable;
import com.graann.treeloader.TreeLoader;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

/**
 * @author gromova on 20.09.17.
 */
public class TreePaneWidget implements Viewable<JComponent> {
	private TreeLoader loader;
	private TreeWidgetFactory treeWidgetFactory;
	private FilterTreeWidget filterTreeWidget;
	private String fileName;

	private JPanel panel;

	void setLoader(TreeLoader loader) {
		this.loader = loader;
	}

	void setTreeWidgetFactory(TreeWidgetFactory treeWidgetFactory) {
		this.treeWidgetFactory = treeWidgetFactory;
	}

	void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public JComponent getView() {
		return panel;
	}

	void initialize() {
		filterTreeWidget = treeWidgetFactory.create(loader.loadTreeStructure(fileName));

		panel = new JPanel(new MigLayout("fill, flowy"));
		panel.setPreferredSize(new Dimension(800, 600));

		panel.add(filterTreeWidget.getView(), "grow, span 2");
	}

	@Override
	public void destroy() {
		filterTreeWidget.destroy();
	}
}

