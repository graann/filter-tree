package com.graann.laf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.IconUIResource;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class LAFUtils {
	private static final Logger LOG = LoggerFactory.getLogger(LAFUtils.class);


	public static UIDefaults.LazyValue loadIcon(final String resourceName) {
		return table -> loadIcon(LAFUtils.class, resourceName);
	}

	public static IconUIResource loadIcon(final Class<?> baseClass, final String resourceName) {
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
}
