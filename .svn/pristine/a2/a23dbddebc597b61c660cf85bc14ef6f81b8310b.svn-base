package edu.rice.comp504.model.strategy.updateStrategy;

import edu.rice.comp504.model.paint.Ball;


/**
 * Abstract class allows us to refer to wrapper methods the UpdateStrategyUnwrapper.
 */
public abstract class IUpdateWrapperStrategy implements IUpdateStrategy {

    public IUpdateStrategy wrapped_strategy;

    /**
     * Get the interaction strategy name.
     * @return The interaction strategy name.
     */
    public abstract String getName();

    /**
     * Get the wrapped strategy
      * @return the wrapped strategy
     */
    public IUpdateStrategy getWrappedStrategy() {return this.wrapped_strategy;}

    /**
     * set the wrapped strategy
      * @param new_wrap
     */
    public void setWrappedStrategy(IUpdateStrategy new_wrap) {this.wrapped_strategy = new_wrap;}

    public abstract void updateState(Ball context);
}