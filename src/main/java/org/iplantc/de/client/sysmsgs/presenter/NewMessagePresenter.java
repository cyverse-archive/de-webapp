package org.iplantc.de.client.sysmsgs.presenter;

import org.iplantc.core.uicommons.client.ErrorHandler;
import org.iplantc.core.uicommons.client.events.EventBus;
import org.iplantc.core.uicommons.client.info.AnnouncementId;
import org.iplantc.core.uicommons.client.info.AnnouncementRemovedEvent;
import org.iplantc.core.uicommons.client.info.IplantAnnouncementConfig;
import org.iplantc.core.uicommons.client.info.IplantAnnouncer;
import org.iplantc.de.client.I18N;
import org.iplantc.de.client.events.NewSystemMessagesEvent;
import org.iplantc.de.client.events.ShowSystemMessagesEvent;
import org.iplantc.de.client.sysmsgs.services.Services;
import org.iplantc.de.client.sysmsgs.view.Factory;
import org.iplantc.de.client.sysmsgs.view.NewMessageView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * An object of this class manages the interactions of a NewMessageView view.
 * 
 * When an object of this class is no longer needed, the object's tearDown() method should be called
 * to free resources.
 */
public final class NewMessagePresenter implements NewMessageView.Presenter {

    private static final Services SERVICES = GWT.create(Services.class);
    private static final Factory VIEW_FACTORY = GWT.create(Factory.class);
    
    private final NewMessageView view;
    private final EventBus eventBus;
    private final IplantAnnouncer announcer;
    private final IplantAnnouncementConfig annCfg;
    private final HandlerRegistration arrivalReg;

    private AnnouncementId currentAnnId;
    private HandlerRegistration removalReg;

    /**
     * the constructor
     * 
     * @param eventBus the event bus used by the application
     * @param announcer the particular announcer that will contain the view managed by this presenter.
     */
    public NewMessagePresenter(final EventBus eventBus, final IplantAnnouncer announcer) {
        view = VIEW_FACTORY.makeNewMessageView(this);
        this.eventBus = eventBus;
        this.announcer = announcer;
        annCfg = new IplantAnnouncementConfig(true, 0);
        currentAnnId = null;
        removalReg = null;
        arrivalReg = eventBus.addHandler(NewSystemMessagesEvent.TYPE, new NewSystemMessagesEvent.Handler() {
            @Override
            public void onUpdate(final NewSystemMessagesEvent event) {
                announceArrival();
            }
        });
    }

    /**
     * Releases resources consumed by the object. This should be called when the object is no
     * longer needed
     */
    public void tearDown() {
        arrivalReg.removeHandler();
        if (removalReg != null) {
            removalReg.removeHandler();
        }
    }

    /**
     * @see Object#finalize()
     */
    @Override
    protected void finalize() {
        try {
            tearDown();
            super.finalize();
        } catch (final Throwable e) {}
    }

    /**
     * @see NewMessageView.Presenter#handleDisplayMessages()
     */
    @Override
    public void handleDisplayMessages() {
        eventBus.fireEvent(new ShowSystemMessagesEvent());
        announcer.unschedule(currentAnnId);
    }

    private void announceArrival() {
        if (!isAnnouncementScheduled()) {
            currentAnnId = announcer.schedule(view, annCfg);
            removalReg = eventBus.addHandler(AnnouncementRemovedEvent.TYPE, new AnnouncementRemovedEvent.Handler() {
                @Override
                public void onRemove(final AnnouncementRemovedEvent event) {
                    handleAnnouncementRemovalEvent(event);
                }
            });
        }
    }

    private void handleAnnouncementRemovalEvent(final AnnouncementRemovedEvent event) {
        if (event.getAnnouncement().equals(currentAnnId)) {
            if (event.wasAnnounced()) {
                SERVICES.markAllReceived(new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(final Throwable cause) {
                        ErrorHandler.post(I18N.ERROR.markMessageReceivedFailed(), cause);
                        finishRemoval();
                    }
                    @Override
                    public void onSuccess(Void unused) {
                        finishRemoval();
                    }
                });
            } else {
                finishRemoval();
            }
        }
    }

    private void finishRemoval() {
        removalReg.removeHandler();
        removalReg = null;
        currentAnnId = null;
    }

    private boolean isAnnouncementScheduled() {
        return currentAnnId != null;
    }

}
