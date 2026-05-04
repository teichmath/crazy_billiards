package edu.rice.comp504.model.paint;

import edu.rice.comp504.model.strategy.IInteractStrategy;
import edu.rice.comp504.model.strategy.IUpdateStrategy;

import java.awt.*;
import java.util.Observable;

/**
 * The ball that will be drawn and updated on the canvas.
 */
public class Ball {
    private int radius;
    private Point loc;
    private Point vel;
    private String color;
    private IUpdateStrategy uStrategy;
    private IInteractStrategy iStrategy;


    /**
     * Constructor for Ball
     * @param loc The paint location.  The origin (0,0) is the upper left corner of the canvas.
     * @param radius  The paint radius.
     * @param vel The paint velocity.  The velocity is a vector with an x and y component.
     */
    public Ball(Point loc, int radius, Point vel, String color, IUpdateStrategy uStrategy, IInteractStrategy iStrategy) {
    }

    /**
     * Get the radius of the paint.
     * @return The paint radius.
     */
    public int getRadius() { return -1; }

    /**
     * Set the radius of the paint.
     * @param r The paint radius.
     */
    public void setRadius(int r) { }

    /**
     * Get the ball location in the paint world.
     * @return The ball location.
     */
    public Point getLocation() { return null; }


    /**
     * Set the ball location in the canvas.  The origin (0,0) is the top left corner of the canvas.
     * @param loc  The ball x,y coordinate.
     */
    public void setLocation(Point loc) { }

    /**
     * Get the ball color
     * @return The ball color
     */
    public String getColor() {
        return null;
    }

    /**
     * Set the ball color
     * @param color The new color
     */
    public void setColor(String color) {
    }

    /**
     * Compute the next location of the paint in the paint world given the velocity
     * @param velX
     * @param velY
     */
    public void nextLocation(int velX, int velY) {
    }

    /**
     * Get the velocity of the ball.
     * @return The ball velocity
     */
    public  Point getVelocity() { return null; }

    /**
     * Set the ball velocity.
     * @param vel The new ball velocity.
     */
    public void setVelocity(Point vel) { }

    /**
     * Get the ball strategy.
     * @return The ball strategy.
     */
    public IUpdateStrategy getUpdateStrategy() { return null; }

    /**
     * Set the ball strategy to the new strategy.
     * @param strategy  The new strategy.
     */
    public void setUpdateStrategy(IUpdateStrategy strategy) { }

    /**
     * Get the ball-to-ball interaction strategy.
     * @return  The ball-to-ball interaction strategy.
     */
    public IInteractStrategy getInteractStrategy() {
        return null;
    }

    /**
     * Set the ball-to-ball interaction strategy.
     * @param iStrategy  The new ball-to-ball interaction strategy
     */
    public void setInteractStrategy(IInteractStrategy iStrategy) {
    }

    /**
     * Rotate the paint.
     * @param angle  The angle that determines how far to rotate the paint.
     */
    public void rotate(double angle) {
    }

    /**
     * Detects collision between a ball and a wall in the ball world.  Change direction if ball collides with a wall.
     * @param dims The canvas dimensions
     */
    public boolean wallCollision(Point dims) {
        return false;
    }

    /**
     * Detects collision between two balls in the ball world.  Change direction if ball collides with a ball.
     */
    public boolean ballCollision() {
        return false;
    }

    /**
     * Update the state of the paint using strategies associated with the paint.
     */
    public void update(Observable obs, Object o) {

    }
}
