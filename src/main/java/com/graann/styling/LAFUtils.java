package com.graann.styling;

import com.graann.App;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.IconUIResource;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author gromova on 20.09.17.
 */

public class LAFUtils {
	private static final Logger LOG = LoggerFactory.getLogger(LAFUtils.class);

	public static void initLaf() {
		try {
			Font font = Font.createFont(Font.TRUETYPE_FONT, App.class.getResource("/ionicons.ttf").openStream());
			GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
			genv.registerFont(font);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			UIManager.setLookAndFeel("com.graann.styling.MyLookAndFeel");
		} catch (Exception e_) {
			try {
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			}  catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static Image getImageIcon() {
		URL iconURL = LAFUtils.class.getResource("/icon.png");
		return new ImageIcon(iconURL).getImage();
	}

	public static String replacePattern(String pattern, String s, Color color) {
		return "<html>" + s.replace(pattern, "<font color='" + getHex(color) + "'>" + pattern + "</font>") + "</html>";
	}

	private static String getHex(Color color) {
		return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
	}

	public static UIDefaults.LazyValue loadIcon(final String resourceName) {
		return table -> loadIcon(LAFUtils.class, resourceName);
	}

	static IconUIResource loadIcon(final Class<?> baseClass, final String resourceName) {
		try (InputStream imageStream = baseClass.getResourceAsStream(resourceName)) {
			if (imageStream == null) {
				LOG.warn("Image is not found {}", resourceName);
				return null;
			}

			BufferedImage image = ImageIO.read(imageStream);

			return new IconUIResource(new ImageIcon(image));
		} catch (IOException ex) {
			LOG.warn("Failed to load image {}", resourceName, ex);
			return null;
		}
	}

	public static void setTooltipIfNeeded(JLabel label) {
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
