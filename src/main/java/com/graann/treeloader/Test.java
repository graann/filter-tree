package com.graann.treeloader;

import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

import javax.swing.tree.TreeModel;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author gromova on 20.09.17.
 */
public class Test {
	private static BehaviorSubject<List<String>> listBehaviorSubject;

	public static Observable<List<String>> loadFromZDB() {
		if(listBehaviorSubject != null) {
			return listBehaviorSubject;
		}

		listBehaviorSubject = BehaviorSubject.create();
		Observable.<List<String>>create(subscriber -> {

			try (Stream<String> stream = Files.lines(Paths.get("tree.txt"))) {

				subscriber.onNext(stream.filter(s -> !s.isEmpty()).map(String::trim).collect(Collectors.toList()));
			} catch (Exception e) {
				e.printStackTrace();
				subscriber.onError(e);
			}
		}).observeOn(Schedulers.io()).first().subscribe(strings -> listBehaviorSubject.onNext(strings));
		return listBehaviorSubject;
	}

	public static void saveStructure() {
		listBehaviorSubject.subscribeOn(Schedulers.io()).subscribe(strings -> {

			try {
				File file = new File("outputFile1.txt");

				if (!file.exists()) {
					file.createNewFile();
				}

				BufferedWriter bw = new BufferedWriter
						(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));

				int level = 0;
				for (String string : strings) {
					bw.write(getString(level)+string+"\n");
					level = getLevel(level);
				}

				bw.close();

				System.out.println("Done");
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	private static int getLevel(int current) {
		if (Math.random() < 0.1) {
			return (int) (Math.random() * 7)+1;
		}
		if (Math.random() < 0.3) {
			return current + 1;
		}

		if (Math.random() < 0.5) {
			return 1;
		}

		if (Math.random() < 0.8) {
			return current;
		}

		return current > 2 ? current - 1 : 1;
	}

	private static Map<Integer, String> stringMap = new HashMap<>();

	private static String getString(int level) {
		if (stringMap.containsKey(level)) {
			return stringMap.get(level);
		}

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < level; i++) {
			builder.append('+');
		}
		String s = builder.toString();

		stringMap.put(level, s);
		return s;
	}
}