package com.graann.tree.model.filter;

import com.graann.treeloader.TreeStructure;
import rx.Observable;

import javax.swing.tree.TreeNode;

/**
 * @author gromova on 22.09.17.
 */
public interface Filter {
	Observable<TreeNode> rootObservable(TreeStructure structure);
}
