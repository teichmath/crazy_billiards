package edu.rice.comp504.model.strategy.updateStrategy;

import edu.rice.comp504.model.paint.Ball;

/**
 * Standard billiard movement with a high cloth-friction multiplier applied in
 * applyPhysics — the ball decelerates much faster than a normal billiard ball.
 */
public class DragStrategy implements IUpdateStrategy {

    public String getName() { return "drag"; }

    public void updateState(Ball context) {
        context.nextLocation(context.getVelocity().getX(), context.getVelocity().getY());
    }
}
