package com.graann.tree.filter;

import com.graann.treeloader.TreeStructure;
import org.reactfx.util.Tuple2;
import org.reactfx.util.Tuple3;
import rx.Observable;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.List;
import java.util.Set;

/**
 * @author gromova on 25.09.17.
 */
interface TreeNodeFilter {
	Observable<Tuple3<TreeNode, List<DefaultMutableTreeNode>, Integer>> rootObservable(TreeStructure treeStructure, Set<String> filtered);

}
