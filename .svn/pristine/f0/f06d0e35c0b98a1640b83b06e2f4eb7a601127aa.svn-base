package edu.rice.comp504.model.strategy.interactStrategy;

import edu.rice.comp504.model.paint.Ball;


/**
 * The null strategy among the interaction strategy classes.
 */
public class NullInteractStrategy implements IInteractStrategy {
    private static IInteractStrategy my_iinteract;

    /**
     * Constructor
     */
    private NullInteractStrategy() {

    }

    /**
     * makeStrategy allows us to create a singleton of this class.
     * @return the singleton.
     */
    public static IInteractStrategy makeStrategy() {
        if(my_iinteract == null) {
            my_iinteract = new NullInteractStrategy();
        }
        return my_iinteract;
    }

    /**
     * get the name of the strategy.
     * @return the name
     */
    @Override
    public String getName() {
        return "null_interact";
    }

    /**
     * Do nothing. (It's null.)
     * @param src  The src ball will impose the interaction strategy on the dest ball.
     * @param dest The dest ball behavior will be affected by the src ball interaction strategy
     */
    @Override
    public void interact(Ball src, Ball dest) {
    }
}
