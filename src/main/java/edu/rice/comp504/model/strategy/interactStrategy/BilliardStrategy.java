package edu.rice.comp504.model.strategy.interactStrategy;

import edu.rice.comp504.model.paint.Ball;

import java.awt.geom.Point2D;

/**
 * Simulates an elastic collision; transfers force from an interaction to both balls.
 */
public class BilliardStrategy implements IInteractStrategy {

    private double forceX = 0;
    private double forceY = 0;
    private InteractStrategyUnwrapper my_int_unwrapper;

    /**
     * Constructor
     */
    public BilliardStrategy() {
        my_int_unwrapper = InteractStrategyUnwrapper.makeUnwrapper();
    }


    /**
     * Get the interaction strategy name.
     * @return The interaction strategy name.
     */
    public String getName() {return "billiard";}

    /**
     * Stores a velocity change to be applied later.
     * @param addx
     * @param addy
     */
    public void addForce(double addx, double addy) {
        this.forceX += addx;
        this.forceY += addy;
    }

    public double getForceX() { return forceX; }
    public double getForceY() { return forceY; }

    public void zeroForce() { forceX = 0; forceY = 0; }

    /**
     * The billiard ball (src) will accelerate itself and the dest ball as though the dest ball
     * were at rest; if the dest ball later calls this method on the src ball, the two effects will
     * combine to make a true elastic collision.
     *
     * * @param src  The src ball will impose the interaction strategy on the dest ball.
     * @param dest The dest ball behavior will be affected by the src ball interaction strategy
     */
    public void interact(Ball src, Ball dest) {

        // Unit normal from src center toward dest center
        double nx = dest.getLocation().getX() - src.getLocation().getX();
        double ny = dest.getLocation().getY() - src.getLocation().getY();
        double dist = Math.sqrt(nx * nx + ny * ny);
        if (dist < 0.001) return;
        nx /= dist;
        ny /= dist;

        // Project src velocity onto normal; skip if src is already moving away from dest
        double proj = src.getVelocity().getX() * nx + src.getVelocity().getY() * ny;
        if (proj <= 0) return;

        for (int i = 0; i < 2; i++) {
            int a = (i == 0) ? -1 : 1;
            Ball b = (i == 0) ? src : dest;
            if (b.getInteractStrategy().getName().contains("billiard")) {
                BilliardStrategy strat = (BilliardStrategy)
                        my_int_unwrapper.getBaseInteractStrategy(b, "billiard");
                strat.addForce(a * proj * nx, a * proj * ny);
            } else {
                b.setVelocity(new Point2D.Double(
                        b.getVelocity().getX() + a * proj * nx,
                        b.getVelocity().getY() + a * proj * ny));
            }
        }

    }


}
