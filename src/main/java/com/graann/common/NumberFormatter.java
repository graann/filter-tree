package com.graann.common;

import java.text.DecimalFormat;

public class NumberFormatter extends DecimalFormat {
	public NumberFormatter() {
		setMinimumFractionDigits(0);
		setMaximumFractionDigits(0);
	}
}
