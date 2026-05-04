package edu.rice.comp504.model.strategy.updateStrategy;


import edu.rice.comp504.model.paint.Ball;

/**
 * The pulse strategy will increase, then decrease the radius of the ball.
 * This occurs at regular intervals.
 * We don't use a singleton in this case, because there are fields in the strategy that should not be shared
 * among balls using this strategy.
 */
public class PulseStrategy implements IUpdateStrategy {

    private int pulse_clock;

    /**
     * Constructor
     */
    public PulseStrategy() {
        pulse_clock = 0;
    }


    /**
     * Get the strategy name
     * @return strategy name
     */
    public String getName() {
        return "pulse";
    }

    /**
     * Update the ball state in the ball world
     * @param context The ball to update
     */
    public void updateState(Ball context) {
        pulse_clock++;
        double scale_change = 0;
        if (pulse_clock == 8) {
            scale_change = 1.4;
        }
        if(pulse_clock == 10) {
            scale_change = 1/1.4;
            pulse_clock = 0;
        }
        if(scale_change != 0) context.setRadius((int)(context.getRadius() * scale_change));
    }
}
