package com.graann.components;

import com.graann.common.Viewable;
import com.graann.styling.ColorScheme;
import com.graann.styling.FontIcon;
import com.graann.styling.IconFontSymbols;

import javax.swing.*;
import java.beans.PropertyChangeListener;

public class FilterBox implements Viewable<JComponent> {
	public enum State {
		ERROR,
		SUCCESS
	}

	private JLabel label;
	private State state = State.SUCCESS;

	public FilterBox() {
		label = WidgetFactory.createIconLabel(IconFontSymbols.SEARCH);
		label.setForeground(ColorScheme.MAINT);
		label.setIcon(FontIcon.builder().symbol(IconFontSymbols.SEARCH.getString())
				.color(ColorScheme.LEAF_ICON).build());

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
		this.state = state;
		label.setForeground(state == State.ERROR ? ColorScheme.ERROR : ColorScheme.MAINT);
	}

	@Override
	public void destroy() {

	}
}
