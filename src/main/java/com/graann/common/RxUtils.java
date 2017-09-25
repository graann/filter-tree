package com.graann.common;

import rx.Subscription;

/**
 * @author gromova on 25.09.17.
 */
public class RxUtils {
	public static void unsubscribe(Subscription subscription) {
		if (subscription != null && !subscription.isUnsubscribed()) {
			subscription.unsubscribe();
		}
	}
}
