package com.graann.tree.model;

import com.graann.common.Destroyable;
import rx.Observable;

import java.util.Set;

/**
 * @author gromova on 25.09.17.
 */
public interface StringFilter extends Destroyable{
	Observable<Set<String>> appropriateStringObservable(String pattern);
}
