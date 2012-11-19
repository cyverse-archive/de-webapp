package org.iplantc.de.client.utils;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Timer;

/**
 * Periodically performs registered tasks.
 */
public class TaskRunner {

    /**
     * The default interval for repeating tasks.
     */
    private static final int DEFAULT_INTERVAL = 15000;

    /**
     * The single instance of this class.
     */
    private static TaskRunner instance;

    /**
     * The list of tasks to perform.
     */
    private final List<Runnable> tasks = new ArrayList<Runnable>();

    /**
     * The timer to use for repeating tasks.
     */
    private final Timer timer;

    public TaskRunner(int interval) {
        timer = new Timer() {
            
            @Override
            public void run() {
                // TODO Auto-generated method stub
                
            }
        };
        
    }

    /**
     * @return the single instance of this class.
     */
    public static TaskRunner getInstance() {
        if (instance == null) {
            instance = new TaskRunner();
        }
        return instance;
    }

    /**
     * Adds a task to the list of tasks to be performed.
     * 
     * @param task the task to add.
     */
    public void addTask(Runnable task) {
        if (task == null) {
            throw new NullPointerException("the task may not be null");
        }
        tasks.add(task);
    }

    /**
     * Removes a task from the list of tasks to be performed.
     * 
     * @param task the task to remove.
     */
    public void removeTask(Runnable task) {
        if (task == null) {
            throw new NullPointerException("the task may not be null");
        }
        tasks.remove(task);
    }
}
