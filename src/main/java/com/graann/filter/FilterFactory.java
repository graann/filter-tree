package com.graann.filter;

import rx.Observable;

import javax.swing.tree.TreeNode;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author gromova on 22.09.17.
 */
public class FilterFactory {

	public static Observable<Filter> create(TreeNode root) {
		return Observable.create(subscriber -> {
			Map<String, TreeNode> library = new LinkedHashMap<>();



		});

	}
}
