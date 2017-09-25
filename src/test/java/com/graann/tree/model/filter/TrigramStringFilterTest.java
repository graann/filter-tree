package com.graann.tree.model.filter;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author gromova on 25.09.17.
 */
class TrigramStringFilterTest {
	@Test
	void getNGrams() {
		String[] trigrams = {"кол", "олб", "лба", "бас", "аса", "са", "а"};

		Set<String> set = TrigramStringFilter.getNGrams("колбаса", 3);
		assertNotNull(set);
		assertEquals(set.size(), 7);
		for (String trigram : trigrams) {
			assertTrue(set.contains(trigram));
		}
	}
}