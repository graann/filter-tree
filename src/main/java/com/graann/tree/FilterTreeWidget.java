package com.graann.tree;

import com.graann.common.Viewable;
import com.graann.treeloader.TreeLoader;
import net.miginfocom.swing.MigLayout;
import rx.schedulers.Schedulers;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import java.awt.Dimension;

/**
 * @author gromova on 20.09.17.
 */
public class FilterTreeWidget implements Viewable<JComponent> {
	private JPanel panel;
	private JTree jTree;
	private JScrollPane scrollPane;
	private JTextField jTextField = new JTextField();


	public JComponent getView() {
		return panel;
	}

	public void initialize() {

		panel = new JPanel(new MigLayout("fill, flowy"));

		panel.setPreferredSize(new Dimension(800, 600));
		panel.add(jTextField);

		TreeLoader.loadTree().subscribeOn(Schedulers.from(SwingUtilities::invokeLater)).subscribe(strings -> {
			jTree = new JTree(strings);
			scrollPane = new JScrollPane(jTree);

			panel.add(scrollPane, "grow");

		});
	}
}
