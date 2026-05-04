package edu.rice.comp504.model.strategy.interactStrategy;

import edu.rice.comp504.model.paint.Ball;
import edu.rice.comp504.model.strategy.updateStrategy.IUpdateStrategy;

/**
 * Exchanges its update strategy with the ball it hits.
 */
public class SwapperStrategy implements IInteractStrategy {

    private static IInteractStrategy my_iinteract;

    /**
     * Constructor
     */
    private SwapperStrategy() {

    }
    /**
     * Get the interaction strategy name.
     * @return The interaction strategy name.
     */
    public String getName() {return "swapper";}


    /**
     * makeStrategy allows us to create a singleton of this class.
     * @return the singleton.
     */
    public static IInteractStrategy makeStrategy() {
        if(my_iinteract == null) {
            my_iinteract = new SwapperStrategy();
        }
        return my_iinteract;
    }

    /**
     * Swaps the src and dest ball update strategies.
     *
     * * @param src  The src ball will impose the interaction strategy on the dest ball.
     * @param dest The dest ball behavior will be affected by the src ball interaction strategy
     */
    public void interact(Ball src, Ball dest) {

        IUpdateStrategy swapped = dest.getUpdateStrategy();
        dest.setUpdateStrategy(src.getUpdateStrategy());
        src.setUpdateStrategy(swapped);

    }
}

