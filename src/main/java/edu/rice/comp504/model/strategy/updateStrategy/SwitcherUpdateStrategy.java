package edu.rice.comp504.model.strategy.updateStrategy;


import edu.rice.comp504.model.paint.Ball;

/**
 * The SwitcherStrategy serves as a wrapper on a strategy passed to its constructor; this will allow identification
 * switcher balls.
 */
public class SwitcherUpdateStrategy implements IUpdateStrategy {

    private IUpdateStrategy wrapped_strategy;

    /**
     * Constructor
     */
    public SwitcherUpdateStrategy(IUpdateStrategy wrapped_strategy) {
        this.wrapped_strategy = wrapped_strategy;
    }

    /**
     * Get the ball name
     * @return ball name
     */
    public String getName() {
        return "switcher_update "+wrapped_strategy.getName();
    }

    /**
     * Update the ball state in the ball world
     * @param context The ball to update
     */
    public void updateState(Ball context) {
        wrapped_strategy.updateState(context);
    }
}
