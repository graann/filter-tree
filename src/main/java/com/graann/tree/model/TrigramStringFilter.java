package com.graann.tree.model;

import org.apache.commons.collections4.trie.PatriciaTrie;
import rx.Observable;
import rx.Single;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author gromova on 25.09.17.
 */
public class TrigramStringFilter implements StringFilter {
	private final static int N = 3;
	private static BehaviorSubject<PatriciaTrie<Set<String>>> trieBehaviorSubject = BehaviorSubject.create();

	void setStrings(Set<String> strings) {
		computeTrie(strings);
	}

	private void computeTrie(Set<String> strings) {
		Single.<PatriciaTrie<Set<String>>>create(subscriber -> {
				Map<String, Set<String>> map = new LinkedHashMap<>();
				strings.parallelStream().forEach(s -> {
					if(Thread.currentThread().isInterrupted()) {
						return;
					}

					add(map, s, s);

					Set<String> nGrams = getNGrams(s, N);
					nGrams.forEach(nGram -> add(map, nGram, s));
				});
				subscriber.onSuccess(new PatriciaTrie<>(map));
			})
			.subscribeOn(Schedulers.computation())
			.subscribe(setPatriciaTrie -> trieBehaviorSubject.onNext(setPatriciaTrie));
	}

	@Override
	public void destroy() {

	}

	public Observable<Set<String>> appropriateStringObservable(String pattern) {
		if (pattern == null || pattern.isEmpty()) {
			return Observable.just(null);
		}

		return trieBehaviorSubject
				.map(libraryTrie -> {
					String gramKey = pattern.length() <= N ? pattern : pattern.substring(0, N);
					return libraryTrie.prefixMap(gramKey)
							.values()
							.stream()
							.flatMap(Collection::stream)
							.distinct()
							.filter(s -> s.contains(pattern))
							.parallel()
							.collect(Collectors.toSet());
				});
	}

	private static void add(Map<String, Set<String>> map, String key, String value) {
		Set<String> set = map.computeIfAbsent(key, k -> new LinkedHashSet<>());
		set.add(value);
	}

	static Set<String> getNGrams(String text, int n) {
		char[] chars = text.toCharArray();
		Set<String> set = new HashSet<>();
		for (int i = 0; i < chars.length; i++) {
			StringBuilder sb = new StringBuilder();
			for (int j = 0; j < n && i + j < chars.length; j++) {
				sb.append(chars[i + j]);
			}
			set.add(sb.toString());
		}
		return set;
	}
}
