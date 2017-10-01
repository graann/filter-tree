package com.graann;

import com.graann.common.Viewable;
import com.graann.tree.components.DefaultFilterTreeWidgetFactory;
import com.graann.tree.components.FilterTreeWidgetFactory;

import javax.swing.*;
import java.awt.*;

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


		try {
			Font font = Font.createFont(Font.TRUETYPE_FONT, App.class.getResource("/entypo.ttf").openStream());
			GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
			genv.registerFont(font);
		} catch (Exception e) {
			e.printStackTrace();
		}


		try {
			UIManager.setLookAndFeel("com.graann.laf.MyLookAndFeel");
		//	UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
			// UIManager.setLookAndFeel("com.seaglasslookandfeel.SeaGlassLookAndFeel");
		//	UIManager.setLookAndFeel("com.jtattoo.plaf.mcwin.NoireLookAndFeel");
			//UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
	//		UIManager.setLookAndFeel("com.pagosoft.plaf.PgsLookAndFeel");
		//	UIManager.setLookAndFeel("ch.randelshofer.quaqua.jaguar.Quaqua15JaguarLookAndFeel");
		} catch (Exception e_) {
			try {
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			}  catch (Exception e) {
				e.printStackTrace();
			}
		}




		//WebLookAndFeel.install (MyLookAndFeel.class, false);

		Viewable<JComponent> newContentPane = treeFactory.create();
		frame.setContentPane(newContentPane.getView());

		frame.pack();
		frame.setVisible(true);
	}

}
