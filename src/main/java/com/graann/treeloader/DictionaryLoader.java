package com.graann.treeloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.io.InputStream;
import java.util.*;

/**
 * @author gromova on 22.09.17.
 */
public class DictionaryLoader implements TreeLoader {
	private static final Logger LOG = LoggerFactory.getLogger(DictionaryLoader.class);
	private static final String CHARSET_NAME = "UTF8";

	private BehaviorSubject<TreeStructure> treeBehaviorSubject;

	@Override
	public Observable<TreeStructure> loadTreeStructure(String fileName) {
		if (treeBehaviorSubject != null) {
			return treeBehaviorSubject;
		}

		treeBehaviorSubject = BehaviorSubject.create();
		Observable.<TreeStructure>create(subscriber -> subscriber.onNext(read(fileName)))
				.subscribeOn(Schedulers.io())
				.first()
				.subscribe(strings -> treeBehaviorSubject.onNext(strings));

		return treeBehaviorSubject;
	}

	private TreeStructure read(String fileName) {
		Map<String, Set<TreeNode>> map = new LinkedHashMap<>();
		int nodeCounter = 0;

		DefaultMutableTreeNode root = null;
		DefaultMutableTreeNode prev = null;


		try (InputStream resourceAsStream = this.getClass().getResourceAsStream(fileName);
			 Scanner scanner = new Scanner(resourceAsStream, CHARSET_NAME)) {
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
			LOG.error(e.getMessage());
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
