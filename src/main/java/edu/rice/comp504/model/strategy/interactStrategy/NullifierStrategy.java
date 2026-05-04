package edu.rice.comp504.model.strategy.interactStrategy;

import edu.rice.comp504.model.paint.Ball;
import edu.rice.comp504.model.strategy.updateStrategy.NullUpdateStrategy;

import java.awt.*;


/**
 * Imposes a null update strategy on the ball it hits.
 */
public class NullifierStrategy implements IInteractStrategy {

    private static IInteractStrategy my_iinteract;

    /**
     * Constructor
     */
    private NullifierStrategy() {

    }

    /**
     * makeStrategy allows us to create a singleton of this class.
     * @return the singleton.
     */
    public static IInteractStrategy makeStrategy() {
        if(my_iinteract == null) {
            my_iinteract = new NullifierStrategy();
        }
        return my_iinteract;
    }

    /**
     * Get the interaction strategy name.
     * @return The interaction strategy name.
     */
    public String getName() {return "nullifier";}

    /**
     *  sets the dest ball's update strategy to null.
     *
     * * @param src  The src ball will impose the interaction strategy on the dest ball.
     * @param dest The dest ball behavior will be affected by the src ball interaction strategy
     */
    public void interact(Ball src, Ball dest) {
        dest.setVelocity(new Point(0,0));
        dest.setUpdateStrategy(NullUpdateStrategy.makeStrategy());
        dest.setInteractStrategy(NullInteractStrategy.makeStrategy());
    }
}
