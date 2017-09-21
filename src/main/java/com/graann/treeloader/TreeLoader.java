package com.graann.treeloader;

import com.graann.App;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
public class TreeLoader {
	private static BehaviorSubject<List<String>> listBehaviorSubject;
	private static BehaviorSubject<DefaultMutableTreeNode> treeBehaviorSubject;

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


	public static Observable<DefaultMutableTreeNode> loadTree() {
		if(treeBehaviorSubject != null) {
			return treeBehaviorSubject;
		}

		treeBehaviorSubject = BehaviorSubject.create();
		Observable.<DefaultMutableTreeNode>create(subscriber -> {
			subscriber.onNext(read());
		}).observeOn(Schedulers.io()).first().subscribe(strings -> treeBehaviorSubject.onNext(strings));
		return treeBehaviorSubject;
	}

	private static DefaultMutableTreeNode read() {
		DefaultMutableTreeNode prev = null;

		try {
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(
							new FileInputStream("tree.txt"), "UTF8"));

			for (String line; (line = bufferedReader.readLine()) != null; ) {
				int level = getLevel(line);
				String value = line.substring(level + 1);
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(value);
				if (prev == null) {
					prev = node;
				} else {
					if (level > prev.getLevel()) {
						prev.add(node);
					} else if (level == prev.getLevel()) {
						prev = (DefaultMutableTreeNode) prev.getParent();
						prev.add(node);
					} else {
						while (level < prev.getDepth()) {
							prev = (DefaultMutableTreeNode) prev.getParent();
						}
						prev = (DefaultMutableTreeNode) prev.getParent();
						prev.add(node);
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return prev;
	}

	private static int getLevel(String string) {
		int level = 0;
		while (string.charAt(level) == '+') {
			level++;
		}

		return level;
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
