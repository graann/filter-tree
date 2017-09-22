package com.graann.tree;

import com.graann.common.Viewable;
import com.graann.tree.model.FilterTreeModelWrapper;
import com.graann.treeloader.TreeLoader;
import net.miginfocom.swing.MigLayout;
import rx.Observable;
import rx.schedulers.Schedulers;

import javax.swing.*;
import javax.swing.plaf.TreeUI;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author gromova on 20.09.17.
 */
public class FilterTreeWidget implements Viewable<JComponent> {
    private JPanel panel;
    private JTree tree;
    private JScrollPane scrollPane;

    private JTextField jTextField = new JTextField();
    private JButton button = new JButton("expand");

    private FilterTreeModelWrapper factory;

    public void setModelWrapper(FilterTreeModelWrapper factory) {
        this.factory = factory;
    }

    public JComponent getView() {
        return panel;
    }

    public void initialize() {

        panel = new JPanel(new MigLayout("fill, wrap 2", "[min!][]", "[min!][]"));

        panel.setPreferredSize(new Dimension(800, 600));
        panel.add(jTextField, "wmin 100");

        Observable<String> filterObservable = Observable.create(subscriber -> {
            jTextField.addActionListener(e -> subscriber.onNext(jTextField.getText()));
        });

        button.addActionListener(e -> expandAllNodes());
        panel.add(button);

        TreeLoader.loadTree()
                .subscribeOn(Schedulers.from(SwingUtilities::invokeLater))
                .subscribe(model -> {
                    TreeModel filterTreeModel = factory.wrap(model, filterObservable);

                    tree = new JTree(filterTreeModel);
                    expandAllNodes();
                    scrollPane = new JScrollPane(tree);
                    panel.add(scrollPane, "grow, span 2");
                });
    }

    public void expandAllNodes() {
        TreeUI ui = tree.getUI();
//        tree.setUI(null);

        expandAll(tree);

  //      tree.setUI(ui);
    }


    private static void expandAll(JTree tree)
    {

        TreeNode root = (TreeNode) tree.getModel().getRoot();
        List<TreeNode> allLeafNodes = getAllLeafNodes(root);

        List<TreePath> leafNodes = allLeafNodes.stream().map(FilterTreeWidget::getPath).collect(Collectors.toList());

        for (TreePath leafNode : leafNodes) {
            tree.expandPath(leafNode);
        }

/*
        int r = 0;
        while (r < tree.getRowCount())
        {
            tree.expandRow(r);
            r++;
        }
*/
    }

    public static TreePath getPath(TreeNode treeNode) {
        List<Object> nodes = new ArrayList<>();
        if (treeNode != null) {
            nodes.add(treeNode);
            treeNode = treeNode.getParent();
            while (treeNode != null) {
                nodes.add(0, treeNode);
                treeNode = treeNode.getParent();
            }
        }

        return nodes.isEmpty() ? null : new TreePath(nodes.toArray());
    }

    private static List<TreeNode> getAllLeafNodes(TreeNode node) {
        List<TreeNode> leafNodes = new ArrayList<>();

        if (node.isLeaf()) {
            leafNodes.add(node.getParent());
        } else {
            Enumeration children = node.children();
            while(children.hasMoreElements()) {
                leafNodes.addAll(getAllLeafNodes((TreeNode) children.nextElement()));
            }
        }
        return leafNodes;
    }
}

