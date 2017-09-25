package com.graann.tree.model.filter;

import com.graann.treeloader.TreeStructure;
import org.reactfx.util.Tuple2;
import rx.Observable;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.List;
import java.util.Set;

/**
 * @author gromova on 22.09.17.
 */
public interface Filter {
	Observable<Tuple2<DefaultMutableTreeNode, List<DefaultMutableTreeNode>>> rootObservable(Set<String> filtered);
}
