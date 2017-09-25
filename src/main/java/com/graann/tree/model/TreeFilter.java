package com.graann.tree.model;

import com.graann.treeloader.TreeStructure;
import org.reactfx.util.Tuple2;
import rx.Observable;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.List;
import java.util.Set;

/**
 * @author gromova on 25.09.17.
 */
public interface TreeFilter {
	Observable<Tuple2<DefaultMutableTreeNode, List<DefaultMutableTreeNode>>> rootObservable(TreeStructure treeStructure, Set<String> filtered);
}
