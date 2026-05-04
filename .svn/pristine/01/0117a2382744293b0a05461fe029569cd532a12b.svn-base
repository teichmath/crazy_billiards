package edu.rice.comp504.model.cmd;

import edu.rice.comp504.model.DispatchAdapter;
import edu.rice.comp504.model.paint.Ball;

import java.awt.*;

/**
 * This command deletes an observer from the dispatch adapter array when its radius is zero.
 */
public class DeletionCommand implements IBallCmd {

    private Point dims;
    private DispatchAdapter dad;

    /**
     * The constructor
      * @param dims
     * @param dad
     */
    public DeletionCommand(Point dims, DispatchAdapter dad) {
        this.dims = dims;
        this.dad = dad;
    }

    /**
     * Execute the command by the receiver (context): Delete the observer.
     * @param context  The receiver that will execute the command.
     */
    public void execute(Ball context) {
        if (context.getRadius() == 0) dad.deleteObserver(context);
    }
}
