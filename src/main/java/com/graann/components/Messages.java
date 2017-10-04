package com.graann.components;

public enum Messages {
	NOTHING_FOUND("Sorry, nothing found"),
	ENTITIES("Entities: "),
	MATCHES("Matches: ");

	private final String string;

	Messages(String string) {
		this.string = string;
	}

	public String getString() {
		return string;
	}
}
