package edu.rice.comp504.model.strategy.updateStrategy;


import edu.rice.comp504.model.paint.Ball;

import java.awt.*;

/**
 * The deja vu strategy will cause the ball to backtrack by changing its position by some
 * negative scaling of its velocity. This occurs at regular intervals.
 * We don't use a singleton in this case, because there are fields in the strategy that should not be shared
 * among balls using this strategy.
 */
public class DejaVuStrategy implements IUpdateStrategy {

    private int jump_clock;

    /**
     * Constructor
     */
    public DejaVuStrategy() {
        jump_clock = 0;
    }

    /**
     * Get the strategy name
     * @return strategy name
     */
    public String getName() {
        return "dejavu";
    }

    /**
     * Update the ball state in the ball world: backtrack the ball at a regular interval, as described above.
     * @param context The ball to update
     */
    public void updateState(Ball context) {
        context.nextLocation((int) context.getVelocity().getX(), (int) context.getVelocity().getY());
        jump_clock++;
        if (jump_clock > 10) {
            Point newloc = context.getLocation();
            newloc = new Point((int) newloc.getX() - 4 * (int) context.getVelocity().getX(), (int) newloc.getY() - 4 * (int) context.getVelocity().getY());
            context.setLocation(newloc);
            jump_clock = 0;
        }
    }
}
