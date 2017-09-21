package com.graann.tree;

import com.graann.common.Viewable;
import com.graann.tree.model.FilterTreeModelWrapper;
import com.graann.treeloader.TreeLoader;
import net.miginfocom.swing.MigLayout;
import rx.Observable;
import rx.schedulers.Schedulers;

import javax.swing.*;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.Enumeration;

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
                    scrollPane = new JScrollPane(tree);
                    panel.add(scrollPane, "grow, span 2");
                });
    }

    public void expandAllNodes() {
        TreeNode root = (TreeNode) tree.getModel().getRoot();
/*        TreeUI ui = tree.getUI();
        tree.setUI(new BasicTreeUI());*/

        expandAll(tree, new TreePath(root));

       // tree.setUI(ui);
    }

    private void expandAll(JTree tree, TreePath parent) {
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration e = node.children(); e.hasMoreElements(); ) {
                TreeNode n = (TreeNode) e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandAll(tree, path);
            }
        }
        tree.expandPath(parent);
        // tree.collapsePath(parent);
    }
}

