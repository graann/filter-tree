package com.graann.styling;


/**
 * @author gromova on 30.09.17.
 */
public enum IconFontSymbols {
	ARROW_DOWN(0xf35f),
	ARROW_RIGHT(0xf363),
	SEARCH(0xf2f5),
	LEAF(0xf1fd),
	FOLDER(0xf2e0),
	OPEN_FOLDER(0xf139),
	COUNT(0xf453),
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
