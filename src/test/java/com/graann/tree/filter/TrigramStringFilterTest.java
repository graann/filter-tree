package com.graann.tree.filter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;


/**
 * @author gromova on 26.09.17.
 */
public class TrigramStringFilterTest {
	private StringFilter trigramStringFilter;

	private final Object lock = new Object();
	private volatile boolean semaphore = false;

	@Before
	public void setUp() throws Exception {
		Set<String> strings = new HashSet<>();
		strings.addAll(Arrays.asList("колбаса", "столб", "яблоко", "аккорд", "полоса", "самолет"));
		trigramStringFilter = new StringFilterFactory().create(strings);
	}

	@After
	public void tearDown() throws Exception {
		semaphore = false;
	}

	@Test
	public void appropriateStringObservable() throws Exception {
		trigramStringFilter.appropriateStringObservable("са")
				.first()
				.subscribe(strings -> {
					assertTrue(strings.contains("колбаса"));
					assertTrue(strings.contains("полоса"));
					assertTrue(strings.contains("самолет"));

					synchronized (lock) {
						semaphore = true;
						lock.notifyAll();
					}
				});

		synchronized (lock) {
			while (!semaphore) {
				lock.wait(10000);
			}
		}
	}

	@Test
	public void getNGrams() throws Exception {
		String[] trigrams = {"кол", "олб", "лба", "бас", "аса", "са", "а"};

		Set<String> set = TrigramStringFilter.getNGrams("колбаса", 3);
		assertNotNull(set);
		assertEquals(set.size(), 7);
		for (String trigram : trigrams) {
			assertTrue(set.contains(trigram));
		}
	}
}