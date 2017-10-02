package com.graann;

import com.graann.common.Viewable;
import com.graann.styling.LAFUtils;
import com.graann.tree.components.DefaultFilterTreeWidgetFactory;
import com.graann.tree.components.FilterTreeWidgetFactory;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 * @author gromova on 20.09.17.
 */
public class App {
	private static FilterTreeWidgetFactory treeFactory = new DefaultFilterTreeWidgetFactory();

	public static void main(String[] args) {
		createAndShow();
	}

	private static void createAndShow() {
		JFrame frame = new JFrame("Tree");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		LAFUtils.initLaf();
		frame.setIconImage(LAFUtils.getImageIcon());

		Viewable<JComponent> newContentPane = treeFactory.create();
		frame.setContentPane(newContentPane.getView());

		frame.pack();
		frame.setVisible(true);
	}


}
