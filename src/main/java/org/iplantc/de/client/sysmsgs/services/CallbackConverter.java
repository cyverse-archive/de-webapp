package org.iplantc.de.client.sysmsgs.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

/**
 * TODO document
 */
public final class CallbackConverter {

	private final AutoBeanFactory beanFactory;
	
	public CallbackConverter(final AutoBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
	
	public <T> AsyncCallback<String> convert(final AsyncCallback<T> callback, 
			final Class<T> resultClass) {
		return new AsyncCallback<String>() {
			@Override
			public void onFailure(final Throwable caught) {
				callback.onFailure(caught);
			}
			@Override
			public void onSuccess(final String jsonResult) {
				callback.onSuccess(AutoBeanCodex.decode(beanFactory, resultClass, jsonResult).as());
			}};
	}
	
	public AsyncCallback<String> voidResponse(final AsyncCallback<Void> callback) {
		return new AsyncCallback<String>() {
			@Override
			public void onFailure(final Throwable caught) {
				callback.onFailure(caught);
			}
			@Override
			public void onSuccess(final String unused) {
				callback.onSuccess(null);
			}};
	}

}