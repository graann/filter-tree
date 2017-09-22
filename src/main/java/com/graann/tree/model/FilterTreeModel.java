package com.graann.tree.model;

import rx.Observable;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gromova on 21.09.17.
 */
public class FilterTreeModel implements TreeModel {
	private List<TreeModelListener> listeners = new ArrayList<>();
	private TreeModelListener listener = new TreeModelListener() {
		@Override
		public void treeNodesChanged(TreeModelEvent e) {
			for (TreeModelListener listener : listeners) {
				listener.treeNodesChanged(e);
			}
		}

		@Override
		public void treeNodesInserted(TreeModelEvent e) {
			for (TreeModelListener listener : listeners) {
				listener.treeNodesInserted(e);
			}
		}

		@Override
		public void treeNodesRemoved(TreeModelEvent e) {
			for (TreeModelListener listener : listeners) {
				listener.treeNodesRemoved(e);
			}
		}

		@Override
		public void treeStructureChanged(TreeModelEvent e) {
			for (TreeModelListener listener : listeners) {
				listener.treeStructureChanged(e);
			}

		}
	};

	private TreeModel treeModel;
	private Observable<TreeModel> modelObservable;

	public FilterTreeModel(TreeModel treeModel) {
		this.treeModel = treeModel;
	}

	public void setModelObservable(Observable<TreeModel> modelObservable) {
		this.modelObservable = modelObservable;
	}

	public void initialize() {
		modelObservable.subscribe(model -> {
			treeModel.removeTreeModelListener(listener);
			treeModel = model;
			treeModel.addTreeModelListener(listener);
		});
	}

	@Override
	public Object getRoot() {
		return treeModel.getRoot();
	}

	@Override
	public Object getChild(Object parent, int index) {
		return treeModel.getChild(parent, index);
	}

	@Override
	public int getChildCount(Object parent) {
		return treeModel.getChildCount(parent);
	}

	@Override
	public boolean isLeaf(Object node) {
		return treeModel.isLeaf(node);
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		treeModel.valueForPathChanged(path, newValue);
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		return treeModel.getIndexOfChild(parent, child);
	}

	@Override
	public void addTreeModelListener(TreeModelListener l) {
		listeners.add(l);
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l) {
		listeners.remove(l);
	}


}
