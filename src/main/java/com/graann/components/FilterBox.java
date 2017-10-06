package com.graann.components;

import com.graann.common.Viewable;
import com.graann.styling.ColorScheme;
import com.graann.styling.FontIcon;
import com.graann.styling.IconFontSymbols;

import javax.swing.*;

public class FilterBox implements Viewable<JComponent> {
	public enum State {
		ERROR,
		SUCCESS
	}

	private final JLabel label;
	private boolean active;

	public FilterBox() {
		label = WidgetFactory.createIconLabel(IconFontSymbols.SEARCH);
		label.setForeground(ColorScheme.MAINT);

		label.addPropertyChangeListener("text", (e) -> Utils.setTooltipIfNeeded(label));
		label.setIcon(FontIcon.builder().symbol(IconFontSymbols.SEARCH.getString())
				.color(ColorScheme.MAINT).build());
		label.setDisabledIcon(FontIcon.builder().symbol(IconFontSymbols.SEARCH.getString())
				.color(ColorScheme.DISABLED).build());

	}

	@Override
	public JComponent getView() {
		return label;
	}

	public void setText(String text) {
		label.setText(text);
	}

	public void setState(State state) {
		label.setForeground(state == State.ERROR ? ColorScheme.ERROR : ColorScheme.MAINT);
	}

	public void setActive(boolean value) {
		active = value;
		updateStatus();
	}

	private void updateStatus() {
		label.setVisible(active || !isNullOrEmpty(label.getText()));
		label.setEnabled(active);
	}

	private static boolean isNullOrEmpty(String string) {
		return string == null || string.isEmpty();
	}


	@Override
	public void destroy() {

	}
}
