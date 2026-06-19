package edu.rice.comp504.model.cmd;

import edu.rice.comp504.model.PhysicsConfig;
import edu.rice.comp504.model.paint.Ball;

import java.awt.geom.Point2D;

/**
 * Applies a cue impulse to any ball whose area contains the given point.
 * Velocity scales with inverse mass (radius^3). Side-spin is derived from the
 * perpendicular distance between the ball's centre and the cue's line of action:
 * a centre-hit produces no spin; an off-centre hit produces ω_z = −5vd/(2R²).
 */
public class ImpulseCommand implements IBallCmd {

    private static final double R_REF = 20.0; // reference radius for mass normalisation

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
        double cx = context.getLocation().getX();
        double cy = context.getLocation().getY();
        double dx = cx - x;
        double dy = cy - y;
        if (Math.sqrt(dx * dx + dy * dy) > context.getRadius()) return;

        double R = context.getRadius();
        double massScale = Math.pow(R_REF / R, 3);
        double speed = strength * PhysicsConfig.get().cueScale * massScale;

        context.setVelocity(new Point2D.Double(
                Math.cos(angle) * speed,
                Math.sin(angle) * speed));

        // Perpendicular distance from ball centre to cue line (2D cross product).
        // This is the offset that generates side-spin naturally from the geometry.
        double d = dx * Math.sin(angle) - dy * Math.cos(angle);
        d = Math.max(-R * 0.9, Math.min(R * 0.9, d)); // can't hit beyond the edge
        double omegaZ = -(5.0 * speed * d) / (2.0 * R * R);
        context.setOmegaZ(omegaZ);
        context.setOmegaRoll(0.0);
    }
}
