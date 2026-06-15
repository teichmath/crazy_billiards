package edu.rice.comp504.model.cmd;

import edu.rice.comp504.model.paint.Ball;

import java.awt.geom.Point2D;

/**
 * Applies a velocity impulse to any ball whose area contains the given point.
 */
public class ImpulseCommand implements IBallCmd {

    private final double x;
    private final double y;
    private final double angle;
    private final double strength;

    public ImpulseCommand(double x, double y, double angle, double strength) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.strength = strength;
    }

    public void execute(Ball context) throws InterruptedException {
        double dx = context.getLocation().getX() - x;
        double dy = context.getLocation().getY() - y;
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist <= context.getRadius()) {
            double speed = strength * 5.0;
            context.setVelocity(new Point2D.Double(
                    context.getVelocity().getX() + Math.cos(angle) * speed,
                    context.getVelocity().getY() + Math.sin(angle) * speed));
        }
    }
}
