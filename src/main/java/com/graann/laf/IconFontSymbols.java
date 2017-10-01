package com.graann.laf;


/**
 * @author gromova on 30.09.17.
 */
public enum IconFontSymbols {
	ARROW_DOWN(0x25be),
	ARROW_RIGHT(0x25b8),
	SEARCH(0xe70a),
	LEAF(0x1f342),
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
