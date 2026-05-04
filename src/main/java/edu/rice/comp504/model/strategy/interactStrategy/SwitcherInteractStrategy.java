package edu.rice.comp504.model.strategy.interactStrategy;


import edu.rice.comp504.model.paint.Ball;

/**
 * The SwitcherStrategy serves as a wrapper on a strategy passed to its constructor; this will allow identification
 * switcher balls.
 */
public class SwitcherInteractStrategy extends IInteractWrapperStrategy {

    /**
     * Constructor
     */
    public SwitcherInteractStrategy(IInteractStrategy wrapped_strategy) {
        this.wrapped_strategy = wrapped_strategy;
    }

    /**
     * Get the ball name
     * @return ball name
     */
    public String getName() {
        return "switcher_interact "+ wrapped_strategy.getName();
    }

    /**
     * perform the interaction for the strategy(ies) inside the wrapper.
     * @param src  The src ball will impose the interaction strategy on the dest ball.
     * @param dest The dest ball behavior will be affected by the src ball interaction strategy
     */
    public void interact(Ball src, Ball dest) {
        wrapped_strategy.interact(src, dest);
    }
}
