package com.graann.tree.filter;

import com.graann.treeloader.TreeStructure;
import rx.Observable;

import javax.swing.tree.TreeNode;
import java.util.Set;

/**
 * @author gromova on 25.09.17.
 */
interface TreeNodeFilter {
	Observable<TreeNode> rootObservable(TreeStructure treeStructure, Set<String> filtered);
}
