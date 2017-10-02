package com.graann.tree.components;

import javax.swing.*;
import java.awt.*;

/**
 * @author gromova on 02.10.17.
 */

class Utils {
	static void setTooltipIfNeeded(JLabel label) {
		String labelText = label.getText();
		if (labelText == null) {
			label.setToolTipText(null);
			return;
		}

		Icon icon = (label.isEnabled()) ? label.getIcon() : label.getDisabledIcon();
		Rectangle paintViewR = new Rectangle();
		Rectangle paintIconR = new Rectangle();
		Rectangle paintTextR = new Rectangle();

		Insets insets = label.getInsets(null);

		paintViewR.x = insets.left;
		paintViewR.y = insets.top;
		paintViewR.width = label.getWidth() - (insets.left + insets.right);
		paintViewR.height = label.getHeight() - (insets.top + insets.bottom);

		if (icon != null) {
			paintIconR.width = icon.getIconWidth();
			paintIconR.height = icon.getIconHeight();
		}

		FontMetrics fontMetrics = label.getFontMetrics(label.getFont());

		String clipped = SwingUtilities.layoutCompoundLabel(label,
				fontMetrics,
				labelText,
				icon,
				label.getVerticalAlignment(),
				label.getHorizontalAlignment(),
				label.getVerticalTextPosition(),
				label.getHorizontalTextPosition(),
				paintViewR,
				paintIconR,
				paintTextR,
				label.getIconTextGap());

		label.setToolTipText(!labelText.equals(clipped) ? labelText : null);
	}
}
