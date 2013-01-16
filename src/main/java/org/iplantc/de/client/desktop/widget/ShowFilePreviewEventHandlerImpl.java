package org.iplantc.de.client.desktop.widget;

import org.iplantc.core.uidiskresource.client.events.ShowFilePreviewEvent;
import org.iplantc.core.uidiskresource.client.events.ShowFilePreviewEvent.ShowFilePreviewEventHandler;
import org.iplantc.de.client.Constants;
import org.iplantc.de.client.models.ViewerWindowConfig;

final class ShowFilePreviewEventHandlerImpl implements ShowFilePreviewEventHandler {
    private final Desktop desktop;

    ShowFilePreviewEventHandlerImpl(Desktop desktop) {
        this.desktop = desktop;
    }

    @Override
    public void showFilePreview(ShowFilePreviewEvent event) {
        ViewerWindowConfig config = new ViewerWindowConfig();
        config.setFile(event.getFile());
        config.setShowTreeTab(false);
        this.desktop.showWindow(Constants.CLIENT.dataViewerTag(), config);
    }
}