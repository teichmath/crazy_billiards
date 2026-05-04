package edu.rice.comp504.model.strategy.updateStrategy;


import edu.rice.comp504.model.paint.Ball;

/**
 * The straight strategy will cause the ball to travel in a straight line.
 */
public class StraightStrategy implements IUpdateStrategy {

    private static IUpdateStrategy my_iupdate;

    /**
     * Constructor
     */
    private StraightStrategy() {

    }

    /**
     * makeStrategy allows us to create a singleton of this class.
     * @return the singleton.
     */
    public static IUpdateStrategy makeStrategy() {
        if(my_iupdate == null) {
            my_iupdate = new StraightStrategy();
        }
        return my_iupdate;
    }

    /**
     * Get the strategy name
     * @return strategy name
     */
    public String getName() {
        return "straight";
    }

    /**
     * Update the ball state in the ball world: simply apply the velocities.
     * @param context The ball to update
     */
    public void updateState(Ball context) {
        context.nextLocation((int)(context.getVelocity().getX()), (int)(context.getVelocity().getY()));
    }
}
