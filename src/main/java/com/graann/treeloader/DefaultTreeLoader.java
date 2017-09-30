package com.graann.treeloader;

import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author gromova on 22.09.17.
 */
public class DefaultTreeLoader implements TreeLoader {
	private static String FILE_NAME = "/tree_.txt";
	private static String CHARSET_NAME = "UTF8";

	private BehaviorSubject<TreeStructure> treeBehaviorSubject;

	@Override
	public Observable<TreeStructure> loadTreeStructure() {
		if (treeBehaviorSubject != null) {
			return treeBehaviorSubject;
		}

		treeBehaviorSubject = BehaviorSubject.create();
		Observable.<TreeStructure>create(subscriber -> subscriber.onNext(read()))
				.subscribeOn(Schedulers.io())
				.first()
				.subscribe(strings -> treeBehaviorSubject.onNext(strings));

		return treeBehaviorSubject;
	}

	private TreeStructure read() {
		Map<String, Set<TreeNode>> map = new LinkedHashMap<>();

		DefaultMutableTreeNode root = null;
		DefaultMutableTreeNode prev = null;

		try {
			File file = new File( this.getClass().getResource(FILE_NAME).toURI() );
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(
							new FileInputStream(file), CHARSET_NAME));

			for (String line; (line = bufferedReader.readLine()) != null; ) {
				int level = getLevel(line);
				String value = line.substring(level);

				DefaultMutableTreeNode node = new DefaultMutableTreeNode(value);
				Set<TreeNode> treeNodes = map.computeIfAbsent(value, k -> new HashSet<>());

				treeNodes.add(node);

				if (prev == null) {
					root = node;
				} else {
					if (level > prev.getLevel()) {
						prev.add(node);
					} else if (level == prev.getLevel()) {
						prev = (DefaultMutableTreeNode) prev.getParent();
						prev.add(node);
					} else {
						while (level <= prev.getLevel()) {
							prev = (DefaultMutableTreeNode) prev.getParent();
						}
						prev.add(node);
					}
				}
				prev = node;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new TreeStructure(root, map);
	}

	private static int getLevel(String string) {
		int level = 0;
		while (string.charAt(level) == '+') {
			level++;
		}

		return level;
	}
}
