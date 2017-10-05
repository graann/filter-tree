package com.graann.treeloader;

import rx.Observable;

/**
 * @author gromova on 22.09.17.
 */
public interface TreeLoader {
	Observable<TreeStructure> loadTreeStructure(String fileName);
}
