package com.graann.tree.ui;

import com.graann.styling.ColorScheme;
import com.graann.styling.FontIcon;
import com.graann.styling.IconFontSymbols;

import javax.swing.*;
import java.awt.*;

class WidgetFactory {
	private static JLabel createLabel() {
		JLabel label = new JLabel();
		label.setForeground(ColorScheme.MAINT);
		return label;
	}

	static JLabel createWarningLabel(Messages key) {
		JLabel label = createLabel();
		label.setText(key.getString());
		label.setForeground(ColorScheme.WARNING);
		changeFontSize(label, Font.PLAIN, 16);

		return label;
	}

	private static void changeFontSize(JLabel label, int style, int size) {
		Font labelFont = label.getFont();
		label.setFont(new Font(labelFont.getName(), style, size));
	}

	static JLabel createIconLabel(IconFontSymbols key) {
		JLabel label = createLabel();
		label.setIcon(FontIcon.builder().symbol(key.getString())
				.color(ColorScheme.LEAF_ICON).build());
		return label;
	}
}
