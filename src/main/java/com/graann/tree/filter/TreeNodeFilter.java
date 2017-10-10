package com.graann.tree.filter;

import com.graann.treeloader.TreeStructure;
import org.reactfx.util.Tuple3;
import rx.Observable;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Set;

/**
 * @author gromova on 25.09.17.
 */
interface TreeNodeFilter {
	Observable<Tuple3<DefaultMutableTreeNode, Set<DefaultMutableTreeNode>, Integer>> rootObservable(TreeStructure treeStructure, Set<String> filtered);

}
