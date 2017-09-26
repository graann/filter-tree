package com.graann;

import com.graann.common.Viewable;
import com.graann.tree.components.DefaultFilterTreeWidgetFactory;
import com.graann.tree.components.FilterTreeWidgetFactory;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.UIManager;
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
/*		try {
		//	UIManager.setLookAndFeel("com.seaglasslookandfeel.SeaGlassLookAndFeel");
			//UIManager.setLookAndFeel("com.jtattoo.plaf.smart.SmartLookAndFeel");
		//	UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		//	UIManager.setLookAndFeel("Nimbus");
		} catch (Exception e_) {*/
			try {
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			}  catch (Exception e) {
				e.printStackTrace();
			}
	//	}

/*		try {
			for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			// If Nimbus is not available, you can set the GUI to another look and feel.
		}*/

		Viewable<JComponent> newContentPane = treeFactory.create();
		frame.setContentPane(newContentPane.getView());

		frame.pack();
		frame.setVisible(true);
	}

}
