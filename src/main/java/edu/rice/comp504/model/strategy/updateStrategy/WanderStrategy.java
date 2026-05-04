package edu.rice.comp504.model.strategy.updateStrategy;


import edu.rice.comp504.model.paint.Ball;

import java.awt.*;

/**
 * The wander strategy will accelerate the ball in random directions.
 */
public class WanderStrategy implements IUpdateStrategy {

    private static IUpdateStrategy my_iupdate;

    /**
     * Constructor
     */
    private WanderStrategy() {

    }

    /**
     * makeStrategy allows us to create a singleton of this class.
     * @return the singleton.
     */
    public static IUpdateStrategy makeStrategy() {
        if(my_iupdate == null) {
            my_iupdate = new WanderStrategy();
        }
        return my_iupdate;
    }

    /**
     * Get the strategy name
     * @return strategy name
     */
    public String getName() {
        return "wander";
    }

    /**
     * Update the ball state in the ball world: apply acceleration in a random direction and with random magnitude.
     * Slow down the ball once it reaches a certain threshold so that we don't have too much acceleration
     * accumulating in a particular direction.
     * @param context The ball to update
     */
    public void updateState(Ball context) {
        int newvelx = (int) context.getVelocity().getX() + getRnd(-10, 20);
        int newvely = (int) context.getVelocity().getY() + getRnd(-10, 20);
        if (Math.abs(newvelx) > 30) newvelx = (int) (newvelx * 0.8);
        if (Math.abs(newvely) > 30) newvely = (int) (newvely * 0.8);
        context.setVelocity(new Point(newvelx, newvely));
        context.nextLocation((int)(context.getVelocity().getX()), (int)(context.getVelocity().getY()));
    }

    /**
     * Gives a random integer based on specifications.
     * @param base
     * @param limit
     * @return a random integer
     */
    private int getRnd(int base, int limit) {
        return (int)Math.floor(Math.random() * limit + base);
    }
}
