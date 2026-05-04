package edu.rice.comp504.model.strategy.interactStrategy;

import edu.rice.comp504.model.paint.Ball;
import edu.rice.comp504.model.strategy.updateStrategy.FollowerWrapperUpdateStrategy;

/**
 * On collision, a leader compels the other ball to follow it.
 */
public class LeaderStrategy implements IInteractStrategy {

    private static IInteractStrategy my_iinteract;

    /**
     * Constructor
     */
    private LeaderStrategy() {

    }

    /**
     * makeStrategy allows us to create a singleton of this class.
     * @return the singleton.
     */
    public static IInteractStrategy makeStrategy() {
        if(my_iinteract == null) {
            my_iinteract = new LeaderStrategy();
        }
        return my_iinteract;
    }

    /**
     * Get the interaction strategy name.
     * @return The interaction strategy name.
     */
    public String getName() {return "leader";}

    /**
     * Wrap the dest ball update strategy in a follower wrapper.
     *
     * * @param src  The src ball will impose the interaction strategy on the dest ball.
     * @param dest The dest ball behavior will be affected by the src ball interaction strategy
     */
    public void interact(Ball src, Ball dest) {
        if(!dest.getUpdateStrategy().getName().contains("follower")) {
            dest.setUpdateStrategy(new FollowerWrapperUpdateStrategy(dest.getUpdateStrategy(), src));
        }
    }
}
