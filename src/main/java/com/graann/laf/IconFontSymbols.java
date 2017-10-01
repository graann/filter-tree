package com.graann.laf;


/**
 * @author gromova on 30.09.17.
 */
public enum IconFontSymbols {
	ARROW_DOWN(0xe60c),
	ARROW_RIGHT(0xe610),
	;

	private final String symbol;

	IconFontSymbols(int symbol) {
		this.symbol = String.valueOf((char) symbol);
	}

	public char getChar() {
		return symbol.charAt(0);
	}

	public String getString() {
		return symbol;
	}
}
