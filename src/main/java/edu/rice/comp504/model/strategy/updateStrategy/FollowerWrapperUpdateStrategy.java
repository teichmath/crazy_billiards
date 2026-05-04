package edu.rice.comp504.model.strategy.updateStrategy;

import edu.rice.comp504.model.paint.Ball;
import edu.rice.comp504.model.strategy.updateStrategy.IUpdateStrategy;
import edu.rice.comp504.model.strategy.updateStrategy.IUpdateWrapperStrategy;

import java.awt.*;

/**
 * Wrapper for use with the Leader interaction strategy. A "follower" carries this wrapper on its own update strategy,
 * and is thus influenced to follow the leader by an added velocity adjustment.
 */
public class FollowerWrapperUpdateStrategy extends IUpdateWrapperStrategy {
    private Ball leader;

    /**
     * The constructor
     * @param wrapped_strategy
     * @param leader
     */
    public FollowerWrapperUpdateStrategy(IUpdateStrategy wrapped_strategy, Ball leader) {
        this.setWrappedStrategy(wrapped_strategy);
        this.leader = leader;
    }

    /**
     * Get the interaction strategy name.
     * @return The interaction strategy name.
     */
    public String getName(){return "followerwrapper_update "+getWrappedStrategy().getName(); }

    /**
     * Get the ball that is this follower's leader
     * @return the leader ball
     */
    public Ball getLeader() {return this.leader;}


    /**
     * The wrapped strategy is used as usual, but an extra velocity adjustment pushes the context ball towards its leader.
     * @param context The ball to update.
     */
    public void updateState(Ball context) {

        getWrappedStrategy().updateState(context);

        if(leader.getRadius() != 0) {
            double vel_vector_x = leader.getLocation().getX() - context.getLocation().getX();
            double vel_vector_y = leader.getLocation().getY() - context.getLocation().getY();
            context.setVelocity(new Point((int)(0.3*context.getVelocity().getX() + 0.1*vel_vector_x),
                    (int)(0.3*context.getVelocity().getY() + 0.1*vel_vector_y)));
        }


    }

}
