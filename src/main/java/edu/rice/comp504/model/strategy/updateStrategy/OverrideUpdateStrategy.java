package edu.rice.comp504.model.strategy.updateStrategy;
import edu.rice.comp504.model.paint.Ball;

/**
 * The override strategy wrapper allows us to identify elsewhere that the ball with this strategy should not be updated
 * directly.
 *
 *  */
public class OverrideUpdateStrategy implements IUpdateStrategy {

    private IUpdateStrategy wrapped_strategy;

    /**
     * Constructor
     */
    public OverrideUpdateStrategy(IUpdateStrategy wrapped_strategy) {
        this.wrapped_strategy = wrapped_strategy;
    }

    /**
     * Get the ball name
     * @return ball name
     */
    public String getName() {
        return "override_update "+wrapped_strategy.getName();
    }

    /**
     * get the wrapped strategy
      * @return the wrapped strategy
     */
    public IUpdateStrategy getWrappedStrategy() {return wrapped_strategy;}

    /**
     * Do nothing.
     * @param context The ball to update.
     */
    public void updateState(Ball context) {
    }
}

