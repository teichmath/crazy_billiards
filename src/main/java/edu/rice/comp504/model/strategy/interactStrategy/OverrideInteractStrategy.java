package edu.rice.comp504.model.strategy.interactStrategy;


import edu.rice.comp504.model.paint.Ball;

/**
* The override strategy wrapper allows us to identify elsewhere that the ball with this strategy should not interact
 * with other balls.
*
 *  */
public class OverrideInteractStrategy extends IInteractWrapperStrategy {

    /**
     * Constructor
     */
    public OverrideInteractStrategy(IInteractStrategy wrapped_strategy) {
        this.wrapped_strategy = wrapped_strategy;
    }

    /**
     * Get the ball name
     * @return ball name
     */
    public String getName() {
        return "override_interact "+wrapped_strategy.getName();
    }

    /**
     * Do nothing.
     * @param src  The src ball will impose the interaction strategy on the dest ball.
     * @param dest The dest ball behavior will be affected by the src ball interaction strategy
     */
    public void interact(Ball src, Ball dest) {
    }
}

