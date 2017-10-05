package com.graann.components;

import com.graann.common.Viewable;
import com.graann.styling.ColorScheme;
import com.graann.styling.FontIcon;
import com.graann.styling.IconFontSymbols;
import com.sun.istack.internal.Nullable;

import javax.swing.*;
import java.beans.PropertyChangeListener;

public class FilterBox implements Viewable<JComponent> {
	public enum State {
		ERROR,
		SUCCESS
	}

	private final JLabel label;
	private State state = State.SUCCESS;
	private boolean active;

	public FilterBox() {
		label = WidgetFactory.createIconLabel(IconFontSymbols.SEARCH);
		label.setForeground(ColorScheme.MAINT);

		PropertyChangeListener listener = (e) -> {
			Utils.setTooltipIfNeeded(label);
		};

		label.addPropertyChangeListener("text", listener);

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
		label.setIcon(active ? FontIcon.builder().symbol(IconFontSymbols.SEARCH.getString())
				.color(ColorScheme.MAINT).build() : FontIcon.builder().symbol(IconFontSymbols.SEARCH.getString())
				.color(ColorScheme.DISABLED).build() );
	}

	private static boolean isNullOrEmpty(@Nullable String string) {
		return string == null || string.isEmpty();
	}


	@Override
	public void destroy() {

	}
}