package com.graann;

import com.graann.common.Viewable;
import com.graann.styling.LAFUtils;
import com.graann.tree.components.DefaultTreePaneWidgetFactory;
import com.graann.tree.components.TreePaneWidgetFactory;

import javax.swing.*;
import java.awt.*;

/**
 * @author gromova on 20.09.17.
 */
public class App {
	private static final String DICT = "/dict.txt";
	private static final String BIG_FILE_STRUCTURE = "/filestreeBig.txt";
	private static final String FILE_STRUCTURE = "/filestree.txt";

	private static final TreePaneWidgetFactory treeFactory = new DefaultTreePaneWidgetFactory();

	public static void main(String[] args) {
		String fileName = (args != null && args.length > 0) ? args[0] : FILE_STRUCTURE;
		createAndShow(fileName);
	}

	private static void createAndShow(String fileName) {
		JFrame frame = new JFrame("Tree");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setMinimumSize(new Dimension(400, 300));

		LAFUtils.initLaf();
		frame.setIconImage(LAFUtils.getImageIcon());

		Viewable<JComponent> newContentPane = treeFactory.create(fileName);
		frame.setContentPane(newContentPane.getView());

		frame.pack();
		frame.setVisible(true);
	}


}
