package org.iplantc.de.client.sysmsgs.services;

import java.util.LinkedList;
import java.util.Queue;

import com.google.gwt.user.client.Command;

/**
 * An object of this class ensure that only one of the service calls it manages is in flight at a
 * time.
 */
final class CommandSequencer {

    private final Queue<ChainableCommand<?>> commands;
    private final Command execNextCmd;

    /**
     * the constructor
     */
    CommandSequencer() {
        commands = new LinkedList<ChainableCommand<?>>();
        execNextCmd = new Command() {
            @Override
            public void execute() {
                executeNext();
            }
        };
    }

    /**
     * Schedules a command to be executed after all of the other commands it manages have completed.
     * If there are no other commands, the command will be executed immediately.
     * 
     * @param command the command being scheduled. This command should not rely on the browser
     *            refreshing before it is called.
     */
    void schedule(final ChainableCommand<?> command) {
        command.setContinuation(execNextCmd);
        commands.add(command);
        if (commands.peek() == command) {
            command.execute();
        }
    }

    private void executeNext() {
        commands.poll();
        if (commands.peek() != null) {
            commands.peek().execute();
        }
    }

}
