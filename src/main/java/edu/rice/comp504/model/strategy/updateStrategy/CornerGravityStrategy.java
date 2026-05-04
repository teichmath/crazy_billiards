package edu.rice.comp504.model.strategy.updateStrategy;


import edu.rice.comp504.model.paint.Ball;

import java.awt.*;

/**
 * CornerGravityStrategy accelerates the ball toward one of the canvas corners. The choice of which corner
 * changes randomly.
 * We don't use a singleton in this case, because there are fields in the strategy that should not be shared
 * among balls using this strategy.
 */
public class CornerGravityStrategy implements IUpdateStrategy {

    private int x_gravity;
    private int y_gravity;
    private int x_gravity_limit;
    private int y_gravity_limit;

    /**
     * Constructor
     */
    public CornerGravityStrategy() {
        x_gravity = 0;
        y_gravity = 0;
        x_gravity_limit = 20;
        y_gravity_limit = 20;
    }


    /**
     * Get the strategy name
     * @return strategy name
     */
    public String getName() {
        return "cornergravity";
    }

    /**
     * Update the ball state in the ball world: gravity toward a corner is increased up to a limit. Once the limit
     * is reached, there is a random chance on each update to change the corner of attraction.
     * @param context The ball to update
     */
    public void updateState(Ball context) {
        if (x_gravity > x_gravity_limit) x_gravity--;
        if (x_gravity < x_gravity_limit) x_gravity++;
        if (y_gravity > y_gravity_limit) y_gravity--;
        if (y_gravity < y_gravity_limit) y_gravity++;

        context.setVelocity(new Point((int) context.getVelocity().getX() + x_gravity, (int) context.getVelocity().getY() + y_gravity));

        context.nextLocation((int)(context.getVelocity().getX()), (int)(context.getVelocity().getY()));

        if(x_gravity == x_gravity_limit && y_gravity == y_gravity_limit) {
            if(Math.random() < .04) x_gravity_limit = x_gravity_limit * -1;
            if(Math.random() < .04) y_gravity_limit = y_gravity_limit * -1;
        }

    }
}
