package edu.rice.comp504.model.strategy.updateStrategy;

import edu.rice.comp504.model.paint.Ball;

import java.awt.*;

/**
 * Like Straight, but a friction force decelerates the ball to a stop after each impulse.
 * Friction factor is random: low friction drifts visibly before stopping,
 * high friction stops within a fraction of a second.
 */
public class DragStrategy implements IUpdateStrategy {

    private final double frictionFactor;
    private double velX = Double.NaN;
    private double velY = Double.NaN;

    public DragStrategy() {
        // 0.80 = stops in ~3 frames from vel=10; 0.97 = halves in ~22 frames
        frictionFactor = 0.80 + Math.random() * 0.17;
    }

    public String getName() { return "drag"; }

    public void updateState(Ball context) {
        double ctxX = context.getVelocity().getX();
        double ctxY = context.getVelocity().getY();

        if (Double.isNaN(velX)) {
            velX = ctxX;
            velY = ctxY;
        } else {
            // Adopt changes from wall bounces (sign flip) or ball collisions (large delta)
            if ((velX > 0 && ctxX < 0) || (velX < 0 && ctxX > 0) || Math.abs(ctxX - velX) > 1.0) velX = ctxX;
            if ((velY > 0 && ctxY < 0) || (velY < 0 && ctxY > 0) || Math.abs(ctxY - velY) > 1.0) velY = ctxY;
        }

        context.nextLocation((int) Math.round(velX), (int) Math.round(velY));
        velX *= frictionFactor;
        velY *= frictionFactor;
        context.setVelocity(new Point((int) Math.round(velX), (int) Math.round(velY)));
    }
}
