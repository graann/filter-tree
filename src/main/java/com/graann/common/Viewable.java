package com.graann.common;

/**
 * @author gromova on 20.09.17.
 */
public interface Viewable<T> {
	T getView();
	void destroy();
}
