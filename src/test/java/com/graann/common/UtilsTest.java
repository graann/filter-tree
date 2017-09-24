package com.graann.common;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {
	@Test
	void getNGrams() {
		String[] trigrams = {"кол", "олб", "лба", "бас", "аса", "са", "а"};

		Set<String> set = Utils.getNGrams("колбаса", 3);
		assertNotNull(set);
		assertEquals(set.size(), 7);
		for (String trigram : trigrams) {
			assertTrue(set.contains(trigram));
		}
	}

}