package com.graann.laf;


/**
 * @author gromova on 30.09.17.
 */
public enum IconFontSymbols {
	ARROW_DOWN(0xf35f),
	ARROW_RIGHT(0xf363),
	SEARCH(0xf4a4),
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
