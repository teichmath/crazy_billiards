package edu.rice.comp504.model.paint;

import edu.rice.comp504.model.DispatchAdapter;
import edu.rice.comp504.model.PhysicsConfig;
import edu.rice.comp504.model.cmd.IBallCmd;
import edu.rice.comp504.model.strategy.interactStrategy.IInteractStrategy;
import edu.rice.comp504.model.strategy.updateStrategy.IUpdateStrategy;

import java.awt.*;
import java.awt.geom.Point2D;
import edu.rice.comp504.model.BallObservable;
import edu.rice.comp504.model.BallObserver;

/**
 * The ball that will be drawn and updated on the canvas.
 */
public class Ball implements BallObserver {

    private int radius;
    private Point2D.Double loc;
    private Point2D.Double vel;
    private double frictionFactor = 1.0;
    private double omegaZ = 0.0;    // side spin (rad/frame), + = CCW from above
    private double omegaRoll = 0.0; // rolling angular speed (rad/frame); pure roll = |v|/R
    private String color;
    private transient IUpdateStrategy uStrategy;
    private transient IInteractStrategy iStrategy;

    /**
     * Constructor for Ball
     * @param loc The paint location.  The origin (0,0) is the upper left corner of the canvas.
     * @param radius  The paint radius.
     * @param vel The paint velocity.
     */
    public Ball(Point2D.Double loc, int radius, Point2D.Double vel, String color, IUpdateStrategy uStrategy, IInteractStrategy iStrategy) {
        this.loc = loc;
        this.radius = radius;
        this.vel = vel;
        this.color = color;
        this.uStrategy = uStrategy;
        this.iStrategy = iStrategy;
    }

    public int getRadius() { return this.radius; }
    public void setRadius(int r) { this.radius = r; }
    public Point2D.Double getLocation() { return this.loc; }
    public void setLocation(Point2D.Double loc) { this.loc = loc; }
    public String getColor() { return this.color; }
    public void setColor(String color) { this.color = color; }

    public void nextLocation(double velX, double velY) {
        double s = PhysicsConfig.DT_SCALE;
        this.setLocation(new Point2D.Double(this.loc.getX() + velX * s, this.loc.getY() + velY * s));
    }

    // Raw position correction — no dt scaling (used by penetration resolver).
    public void nudge(double dx, double dy) {
        this.setLocation(new Point2D.Double(this.loc.getX() + dx, this.loc.getY() + dy));
    }

    public Point2D.Double getVelocity() { return this.vel; }
    public void setVelocity(Point2D.Double vel) { this.vel = vel; }

    public double getFrictionFactor() { return frictionFactor; }
    public void setFrictionFactor(double f) { frictionFactor = f; }

    public double getOmegaZ() { return omegaZ; }
    public void setOmegaZ(double oz) { omegaZ = oz; }
    public double getOmegaRoll() { return omegaRoll; }
    public void setOmegaRoll(double or) { omegaRoll = or; }

    public IUpdateStrategy getUpdateStrategy() { return this.uStrategy; }
    public void setUpdateStrategy(IUpdateStrategy strategy) { this.uStrategy = strategy; }
    public IInteractStrategy getInteractStrategy() { return this.iStrategy; }
    public void setInteractStrategy(IInteractStrategy strategy) { this.iStrategy = strategy; }

    public void rotate(double angle) {
        double new_vel_x = vel.getX() * Math.cos(angle) - vel.getY() * Math.sin(angle);
        double new_vel_y = vel.getY() * Math.cos(angle) + vel.getX() * Math.sin(angle);
        setVelocity(new Point2D.Double(new_vel_x, new_vel_y));
    }

    /**
     * Cushion bounce: reflects normal velocity, applies friction and spin transfer.
     * eps = restitution, mu_c = cushion friction, gamma = spin→velocity efficiency,
     * beta = spin reduction fraction.
     */
    public boolean wallCollision(Point dims) {
        PhysicsConfig cfg = PhysicsConfig.get();
        final double EPS = cfg.eps, MU_C = cfg.muC, GAMMA = cfg.gamma, BETA = cfg.beta;
        boolean hit = false;
        double vx = vel.getX(), vy = vel.getY();
        double x = loc.getX(), y = loc.getY();
        double oZ = omegaZ;

        // Horizontal cushion (top / bottom): normal = Y, tangential = X
        if (y - radius < 0 || y + radius > dims.y) {
            double vn = vy;  // normal component
            double vt = vx;  // tangential component
            // wallSign: +1 = bottom wall, -1 = top wall
            double wallSign = (y + radius > dims.y) ? 1.0 : -1.0;
            // Contact surface tangential vel = vt - wallSign*R*oZ
            double vtContact = vt - wallSign * radius * oZ;
            double sign = vtContact == 0 ? 0 : Math.signum(vtContact);
            double vnOut = -EPS * vn;
            double vtOut = vt - MU_C * (1 + EPS) * Math.abs(vn) * sign
                               + GAMMA * radius * oZ * (-wallSign);
            double oZOut = (1 - BETA) * oZ - (BETA / radius) * (vt - vtOut);
            if (y - radius < 0) { vnOut = Math.abs(vnOut); y = radius; }
            else                 { vnOut = -Math.abs(vnOut); y = dims.y - radius; }
            vx = vtOut; vy = vnOut; oZ = oZOut;
            hit = true;
        }

        // Vertical cushion (left / right): normal = X, tangential = Y
        if (x - radius < 0 || x + radius > dims.x) {
            double vn = vx;
            double vt = vy;
            // wallSign: +1 = right wall, -1 = left wall
            double wallSign = (x + radius > dims.x) ? 1.0 : -1.0;
            // Contact surface tangential vel = vt + wallSign*R*oZ
            double vtContact = vt + wallSign * radius * oZ;
            double sign = vtContact == 0 ? 0 : Math.signum(vtContact);
            double vnOut = -EPS * vn;
            double vtOut = vt - MU_C * (1 + EPS) * Math.abs(vn) * sign
                               + GAMMA * radius * oZ * wallSign;
            double oZOut = (1 - BETA) * oZ - (BETA / radius) * (vt - vtOut);
            if (x - radius < 0) { vnOut = Math.abs(vnOut); x = radius; }
            else                 { vnOut = -Math.abs(vnOut); x = dims.x - radius; }
            vx = vnOut; vy = vtOut; oZ = oZOut;
            hit = true;
        }

        if (hit) {
            vel = new Point2D.Double(vx, vy);
            loc = new Point2D.Double(x, y);
            omegaZ = oZ;
            omegaRoll = 0.0; // ball slides anew after cushion contact
        }
        return hit;
    }

    public boolean ballCollision(Ball other) { return false; }

    /**
     * Mass-weighted elastic collision with side-spin throw effect.
     * Mass is proportional to radius^3. Returns true if a collision occurred and physics were applied.
     * The dvn <= 0 guard ensures the second back-call from UpdateCommand is a no-op.
     */
    public boolean ballCollision(Ball other, DispatchAdapter dad) {
        double dx = other.loc.x - loc.x;
        double dy = other.loc.y - loc.y;
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist > radius + other.radius || dist < 1e-6) return false;

        // Unit normal from this → other
        double nx = dx / dist, ny = dy / dist;
        // Relative velocity along normal (positive = approaching)
        double dvn = (vel.x - other.vel.x) * nx + (vel.y - other.vel.y) * ny;
        if (dvn <= 0) return false;

        double mA = Math.pow(radius, 3);
        double mB = Math.pow(other.radius, 3);
        double mSum = mA + mB;

        // Normal impulse (coefficient of restitution = 0.95)
        PhysicsConfig cfg = PhysicsConfig.get();
        double J = (1 + cfg.eBall) * mA * mB / mSum * dvn;
        vel       = new Point2D.Double(vel.x       - J / mA * nx, vel.y       - J / mA * ny);
        other.vel = new Point2D.Double(other.vel.x + J / mB * nx, other.vel.y + J / mB * ny);

        // Throw effect from this ball's side spin onto other
        final double MU_B = cfg.muB, ALPHA_B = cfg.alphaB;
        double vNormal = J / mB; // speed other gained along n
        double tx = -ny, ty = nx; // tangent direction
        double vThrow = MU_B * vNormal - ALPHA_B * radius * omegaZ;
        other.vel = new Point2D.Double(other.vel.x + vThrow * tx, other.vel.y + vThrow * ty);
        // Reaction on this (Newton's 3rd)
        vel = new Point2D.Double(vel.x - mB / mA * vThrow * tx, vel.y - mB / mA * vThrow * ty);

        return true;
    }

    public void update(BallObservable obs, Object o) {
        try {
            ((IBallCmd) o).execute(this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
