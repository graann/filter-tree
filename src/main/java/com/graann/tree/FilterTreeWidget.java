package com.graann.tree;

import com.graann.common.Viewable;
import com.graann.tree.model.FilterTreeModelWrapper;
import com.graann.treeloader.TreeLoader;
import net.miginfocom.swing.MigLayout;
import rx.Observable;
import rx.schedulers.Schedulers;

import javax.swing.*;
import javax.swing.tree.TreeModel;
import java.awt.*;

/**
 * @author gromova on 20.09.17.
 */
public class FilterTreeWidget implements Viewable<JComponent> {
    private JPanel panel;
    private JTree jTree;
    private JScrollPane scrollPane;
    private JTextField jTextField = new JTextField();

    private FilterTreeModelWrapper factory;

    public void setModelWrapper(FilterTreeModelWrapper factory) {
        this.factory = factory;
    }

    public JComponent getView() {
        return panel;
    }

    public void initialize() {

        panel = new JPanel(new MigLayout("fill, flowy", "", "[min!][]"));

        panel.setPreferredSize(new Dimension(800, 600));
        panel.add(jTextField, "wmin 100");

        Observable<String> filterObservable = Observable.create(subscriber -> {
            jTextField.addActionListener(e -> subscriber.onNext(jTextField.getText()));
        });

        TreeLoader.loadTree()
                .subscribeOn(Schedulers.from(SwingUtilities::invokeLater))
                .subscribe(model -> {
                    TreeModel filterTreeModel = factory.wrap(model, filterObservable);

                    jTree = new JTree(filterTreeModel);
                    scrollPane = new JScrollPane(jTree);

                    panel.add(scrollPane, "grow");
                });
    }
}
