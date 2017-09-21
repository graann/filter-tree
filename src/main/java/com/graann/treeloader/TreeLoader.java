package com.graann.treeloader;

import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author gromova on 20.09.17.
 */
public class TreeLoader {
	private static BehaviorSubject<List<String>> listBehaviorSubject;

	public static Observable<List<String>> loadTree() {
		if(listBehaviorSubject != null) {
			return listBehaviorSubject;
		}

		listBehaviorSubject = BehaviorSubject.create();
		Observable.<List<String>>create(subscriber -> {
			ClassLoader classloader = Thread.currentThread().getContextClassLoader();
			try (Stream<String> stream = Files.lines(Paths.get(classloader.getResource("zdb-win.txt").toURI()))) {
				subscriber.onNext(stream.filter(s -> !s.isEmpty()).map(String::trim).collect(Collectors.toList()));
			} catch (Exception e) {
				e.printStackTrace();
				subscriber.onError(e);
			}
		}).observeOn(Schedulers.io()).first().subscribe(strings -> listBehaviorSubject.onNext(strings));
		return listBehaviorSubject;
	}
}
