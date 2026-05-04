package edu.rice.comp504.model.strategy.interactStrategy;

import edu.rice.comp504.model.paint.Ball;

/**
 * Abstract class allows us to refer to wrapper methods the InteractStrategyUnwrapper.
 */
public abstract class IInteractWrapperStrategy implements IInteractStrategy {

    public IInteractStrategy wrapped_strategy;

    /**
     * Get the interaction strategy name.
     * @return The interaction strategy name.
     */
    public abstract String getName();

    /**
     * get the wrapped strategy.
      * @return the wrapped strategy
     */
    public IInteractStrategy getWrappedStrategy() {return this.wrapped_strategy;}

    /**
     * set the wrapped strategy.
     * @param new_wrap
     */
    public void setWrappedStrategy(IInteractStrategy new_wrap) {this.wrapped_strategy = new_wrap;}

    /**
     * The interaction strategy when two balls collide
     * @param src  The src ball will impose the interaction strategy on the dest ball.
     * @param dest The dest ball behavior will be affected by the src ball interaction strategy
     */
    public abstract void interact(Ball src, Ball dest);
}