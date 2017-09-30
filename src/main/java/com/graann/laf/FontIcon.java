package com.graann.laf;

import javax.swing.*;
import java.awt.*;

public class FontIcon implements Icon {
	private static final int DEFAULT_FONT_SIZE = 16;
	private static Font ICON_FONT = new Font("Rivolicons-Free", Font.PLAIN, DEFAULT_FONT_SIZE);

	private final String symbol;
	private final Color color;
	private final Dimension size;
	private final int xShift;
	private final int yShift;
	private final int fontSize;
	private final boolean useBaselineDescent;

	public static SymbolFontIconBuilder builder() {
		return new SymbolFontIconBuilder();
	}

	public FontIcon(String symbol, Color color, Dimension size, int xShift, int yShift, int fontSize, boolean useBaselineDescent) {
		this.symbol = symbol;
		this.color = color;
		this.size = size;
		this.xShift = xShift;
		this.yShift = yShift;
		this.fontSize = fontSize;
		this.useBaselineDescent = useBaselineDescent;


	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		if (symbol.isEmpty()) {
			return;
		}

		Graphics2D g2 = (Graphics2D) g.create();

		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setColor(color);
		Font font = fontSize == DEFAULT_FONT_SIZE ? ICON_FONT : ICON_FONT.deriveFont((float) fontSize);
		g2.setFont(font);

		FontMetrics fm = g2.getFontMetrics();

		x += Math.ceil((size.width - fm.stringWidth(symbol)) / 2) + xShift;
		y += Math.ceil((size.height + fm.getHeight()) / 2) + yShift;
		if(useBaselineDescent) {
			y -= fm.getDescent();
		}

		g2.drawString(symbol, x, y);

		g2.dispose();
	}

	@Override
	public int getIconWidth() {
		return size.width;
	}

	@Override
	public int getIconHeight() {
		return size.height;
	}

	public static class SymbolFontIconBuilder {
		private String symbol;
		private Color color = Color.WHITE;
		private Integer width;
		private Integer height;
		private int xShift;
		private int yShift;
		private boolean useBaselineDescent = true;
		private int fontSize = DEFAULT_FONT_SIZE;

		public SymbolFontIconBuilder icon(FontIcon icon) {
			this.symbol = icon.symbol;
			this.color = icon.color;
			this.width = icon.size.width;
			this.height = icon.size.height;
			this.xShift = icon.xShift;
			this.yShift = icon.yShift;
			this.useBaselineDescent = icon.useBaselineDescent;
			this.fontSize = icon.fontSize;
			return this;
		}

		public SymbolFontIconBuilder symbol(String symbol) {
			this.symbol = symbol;
			return this;
		}

		public SymbolFontIconBuilder color(Color color) {
			this.color = color;
			return this;
		}

		public SymbolFontIconBuilder width(Integer width) {
			this.width = width;
			return this;
		}

		public SymbolFontIconBuilder height(Integer height) {
			this.height = height;
			return this;
		}

		public SymbolFontIconBuilder size(Dimension size) {
			width = size.width;
			height = size.height;
			return this;
		}

		public SymbolFontIconBuilder useBaselineDescent(boolean value) {
			this.useBaselineDescent = value;
			return this;
		}

		public SymbolFontIconBuilder xShift(int xShift) {
			this.xShift = xShift;
			return this;
		}

		public SymbolFontIconBuilder yShift(int yShift) {
			this.yShift = yShift;
			return this;
		}

		public SymbolFontIconBuilder fontSize(int fontSize) {
			this.fontSize = fontSize;
			return this;
		}

		public FontIcon build() {
			if (symbol == null) {
				throw new IllegalArgumentException("Icon symbol is not set");
			}

			Font font = fontSize == DEFAULT_FONT_SIZE ? ICON_FONT : ICON_FONT.deriveFont((float) fontSize);
			FontMetrics fm = new Canvas().getFontMetrics(font);

			if (width == null) width = fm.stringWidth(symbol);
			if (height == null) height = fm.getHeight();

			Dimension size = new Dimension(width, height);

			return new FontIcon(
					symbol,
					color,
					size,
					xShift,
					yShift,
					fontSize,
					useBaselineDescent
			);
		}
	}

}
