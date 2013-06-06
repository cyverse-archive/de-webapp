package org.iplantc.de.client.sysmsgs.services;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * A chainable command is a command that executes an asynchronous call. When the callback is
 * received, it executes the next command if one has been set. It then executes the a different,
 * provided callback, passing any received value on to it.
 * 
 * @param <T> the callback parameter type
 */
abstract class ChainableCommand<T> implements Command {

    private final AsyncCallback<T> callback;

    private Command continuation = null;

    /**
     * the constructor
     * 
     * @param callback the callback that will be called when the command is finished.
     */
    ChainableCommand(final AsyncCallback<T> callback) {
        this.callback = callback;
    }

    /**
     * @see Command#execute()
     */
    @Override
    public final void execute() {
        execute(new AsyncCallback<T>() {
            @Override
            public void onFailure(final Throwable caught) {
                callContinuation();
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(final T result) {
                callContinuation();
                callback.onSuccess(result);
            }
        });
    }

    /**
     * This method implements the actual asynchronous call. It is passed a callback that when
     * executed calls the next command is there is one and the callback provided to the constructor.
     * 
     * @param wrappedCallback the wrapped callback
     */
    protected abstract void execute(AsyncCallback<T> wrappedCallback);

    /**
     * Sets the command that will be executed when this command finishes.
     * 
     * @param continuation the command to call next.
     */
    final void setContinuation(final Command continuation) {
        this.continuation = continuation;
    }

    private final void callContinuation() {
        if (continuation != null) {
            continuation.execute();
        }
    }

}