package org.iplantc.de.client.sysmsgs.view;

import java.util.Date;

import org.iplantc.de.client.sysmsgs.view.MessagesView.Factory;
import org.iplantc.de.client.sysmsgs.view.MessagesView.MessageProperties;
import org.iplantc.de.client.sysmsgs.view.MessagesView.Presenter;

import com.google.gwt.text.shared.Renderer;

/**
 * This the factory for creating the default messages view.
 * 
 * @param <M> the type of message to view
 */
public final class DefaultMessagesViewFactory<M> implements Factory<M> {

    /**
     * @see Factory#make(Presenter, MessageProperties, StoreSortInfo, SelectionMode, Renderer<Date>)
     */
    @Override
    public MessagesView<M> make(Presenter<M> presenter, MessageProperties<M> messageProperties, Renderer<Date> activationRenderer) {
        return new DefaultMessagesView<M>(presenter, messageProperties, activationRenderer);
    }

}
