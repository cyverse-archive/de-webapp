package org.iplantc.de.client.controllers;

import org.iplantc.core.uiapplications.client.events.CreateNewWorkflowEvent;
import org.iplantc.core.uiapplications.client.events.handlers.CreateNewWorkflowEventHandler;
import org.iplantc.core.uicommons.client.events.EventBus;
import org.iplantc.de.client.Constants;
import org.iplantc.de.client.dispatchers.WindowDispatcher;

/**
 * This class is responsible for listening to the event bus for events related to launching the Pipeline
 * Editor.
 * 
 * FIXME JDS This class will be deleted soon.
 * 
 * @author jstroot
 * 
 */
public class PipelineController {
    private final class CreateNewWorkflowEventHandlerImpl implements CreateNewWorkflowEventHandler {
        @Override
        public void createNewWorkflow() {
            // Dispatch window display action
            WindowDispatcher dispatcher = new WindowDispatcher();
            dispatcher.dispatchAction(Constants.CLIENT.pipelineEditorTag());

        }
    }

    private static PipelineController instance;

    private PipelineController() {
        initListeners();
    }

    public static PipelineController getInstance() {
        if (instance == null) {
            instance = new PipelineController();
        }
        return instance;
    }

    private void initListeners() {
        EventBus.getInstance().addHandler(CreateNewWorkflowEvent.TYPE,
                new CreateNewWorkflowEventHandlerImpl());

    }

}
