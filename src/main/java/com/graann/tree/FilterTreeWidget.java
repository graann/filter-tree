package com.graann.tree;

import com.graann.common.Viewable;
import com.graann.treeloader.TreeLoader;
import net.miginfocom.swing.MigLayout;
import rx.schedulers.Schedulers;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import java.awt.Dimension;

/**
 * @author gromova on 20.09.17.
 */
public class FilterTreeWidget implements Viewable<JComponent> {

	private JPanel panel = new JPanel(new MigLayout("fill"));
	private JTree jTree;
	private JScrollPane scrollPane;

	public JComponent getView() {
		return panel;
	}

	public void initialize() {
		panel.setPreferredSize(new Dimension(800, 600));

		TreeLoader.loadTree().subscribeOn(Schedulers.from(SwingUtilities::invokeLater)).subscribe(strings -> {
			jTree = new JTree(strings.toArray());
			scrollPane = new JScrollPane(jTree);

			panel.add(scrollPane, "grow");
		});
	}
}
