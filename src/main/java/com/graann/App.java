package com.graann;

import com.graann.common.Viewable;
import com.graann.styling.LAFUtils;
import com.graann.tree.components.DefaultTreePaneWidgetFactory;
import com.graann.tree.components.TreePaneWidgetFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author gromova on 20.09.17.
 */
public class App {
	private static TreePaneWidgetFactory treeFactory = new DefaultTreePaneWidgetFactory();

	public static void main(String[] args) {
		createAndShow();
	}

	private static void createAndShow() {
		JFrame frame = new JFrame("Tree");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setMinimumSize(new Dimension(400, 300));

		LAFUtils.initLaf();
		frame.setIconImage(LAFUtils.getImageIcon());

		Viewable<JComponent> newContentPane = treeFactory.create();
		frame.setContentPane(newContentPane.getView());
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				newContentPane.destroy();
			}
		});

		frame.pack();
		frame.setVisible(true);
	}


}
