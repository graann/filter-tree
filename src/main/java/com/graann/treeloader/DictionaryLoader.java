package com.graann.treeloader;

import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.*;

/**
 * @author gromova on 22.09.17.
 */
public class DictionaryLoader implements TreeLoader {
	private static final String FILE_NAME = "/tree.txt";
	private static final String CHARSET_NAME = "UTF8";

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
		int nodeCounter = 0;

		DefaultMutableTreeNode root = null;
		DefaultMutableTreeNode prev = null;

		try (Scanner scanner = new Scanner(this.getClass().getResourceAsStream(FILE_NAME), CHARSET_NAME)) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				int level = getLevel(line);
				String value = line.substring(level);

				nodeCounter++;
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

		return new TreeStructure(root, map, nodeCounter);
	}

	private static int getLevel(String string) {
		int level = 0;
		while (string.charAt(level) == '+') {
			level++;
		}

		return level;
	}
}
