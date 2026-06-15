package edu.rice.comp504.model.paint;

import edu.rice.comp504.model.DispatchAdapter;
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
        this.setLocation(new Point2D.Double(this.loc.getX() + velX, this.loc.getY() + velY));
    }

    public Point2D.Double getVelocity() { return this.vel; }
    public void setVelocity(Point2D.Double vel) { this.vel = vel; }

    public double getFrictionFactor() { return frictionFactor; }
    public void setFrictionFactor(double f) { frictionFactor = f; }

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
     * Reports collision between a shape and a wall in the shape world.
     * @param dims  The canvas dimensions
     */
    public boolean wallCollision(Point dims) {
        String sides_hit = "";
        if (radius - getLocation().getX() > 0) sides_hit += "l";
        if (getLocation().getX() - (dims.getX() - radius) > 0) sides_hit += "r";
        if (radius - getLocation().getY() > 0) sides_hit += "t";
        if (getLocation().getY() - (dims.getY() - radius) > 0) sides_hit += "b";

        if (sides_hit.length() > 0) {
            double vel_x = vel.getX();
            double vel_y = vel.getY();
            double loc_x = getLocation().getX();
            double loc_y = getLocation().getY();

            if (sides_hit.contains("l")) { vel_x = Math.abs(vel_x); loc_x = radius; }
            if (sides_hit.contains("r")) { vel_x = Math.abs(vel_x) * -1; loc_x = dims.getX() - radius; }
            if (sides_hit.contains("t")) { vel_y = Math.abs(vel_y); loc_y = radius; }
            if (sides_hit.contains("b")) { vel_y = Math.abs(vel_y) * -1; loc_y = dims.getY() - radius; }

            setVelocity(new Point2D.Double(vel_x, vel_y));
            setLocation(new Point2D.Double(loc_x, loc_y));
            return true;
        }
        return false;
    }

    public boolean ballCollision(Ball other) { return false; }

    /**
     * Detects collision between two balls in the ball world.
     */
    public boolean ballCollision(Ball other, DispatchAdapter dad) {
        double distance = Math.sqrt(Math.pow(this.loc.x - other.getLocation().getX(), 2)
                + Math.pow(this.loc.y - other.getLocation().getY(), 2));
        boolean collision = false;
        if (distance <= this.radius + other.getRadius()) collision = true;

        if (collision) {
            double this_vel_x = this.vel.getX();
            double this_vel_y = this.vel.getY();
            double other_vel_x = other.vel.getX();
            double other_vel_y = other.vel.getY();

            if (this.loc.x < other.getLocation().getX()) {
                if (this_vel_x > 0) this_vel_x *= -1;
                if (other_vel_x < 0) other_vel_x *= -1;
            }
            if (this.loc.x > other.getLocation().getX()) {
                if (this_vel_x < 0) this_vel_x *= -1;
                if (other_vel_x > 0) other_vel_x *= -1;
            }
            if (this.loc.y < other.getLocation().getY()) {
                if (this_vel_y > 0) this_vel_y *= -1;
                if (other_vel_y < 0) other_vel_y *= -1;
            }
            if (this.loc.y > other.getLocation().getY()) {
                if (this_vel_y < 0) this_vel_y *= -1;
                if (other_vel_y > 0) other_vel_y *= -1;
            }

            this.setVelocity(new Point2D.Double(this_vel_x, this_vel_y));
            other.setVelocity(new Point2D.Double(other_vel_x, other_vel_y));
        }
        return collision;
    }

    public void update(BallObservable obs, Object o) {
        try {
            ((IBallCmd) o).execute(this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
