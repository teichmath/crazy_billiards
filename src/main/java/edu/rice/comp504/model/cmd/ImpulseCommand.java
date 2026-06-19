package edu.rice.comp504.model.cmd;

import edu.rice.comp504.model.paint.Ball;

import java.awt.geom.Point2D;

/**
 * Applies a cue impulse to any ball whose area contains the given point.
 * Velocity scales with inverse mass (radius^3); side-spin is set from the
 * horizontal offset (spin in [-1, 1] maps to ±R/2 off-center hit).
 */
public class ImpulseCommand implements IBallCmd {

    private static final double R_REF = 20.0; // reference radius for mass normalisation

    private final double x;
    private final double y;
    private final double angle;
    private final double strength;
    private final double spin; // normalised offset: -1 (full left) to +1 (full right)

    public ImpulseCommand(double x, double y, double angle, double strength, double spin) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.strength = strength;
        this.spin = spin;
    }

    public void execute(Ball context) throws InterruptedException {
        double dx = context.getLocation().getX() - x;
        double dy = context.getLocation().getY() - y;
        if (Math.sqrt(dx * dx + dy * dy) > context.getRadius()) return;

        double R = context.getRadius();
        // Heavier (larger) balls receive less speed from the same force
        double massScale = Math.pow(R_REF / R, 3);
        double speed = strength * 5.0 * massScale;

        // Set velocity (cue replaces current motion)
        context.setVelocity(new Point2D.Double(
                Math.cos(angle) * speed,
                Math.sin(angle) * speed));

        // Side spin from off-centre hit: omega_z = -(5 * v * d) / (2 * R^2)
        double offset = spin * R * 0.5; // max offset = R/2
        double omegaZ = -(5.0 * speed * offset) / (2.0 * R * R);
        context.setOmegaZ(omegaZ);
        context.setOmegaRoll(0.0); // starts sliding, not rolling
    }
}
