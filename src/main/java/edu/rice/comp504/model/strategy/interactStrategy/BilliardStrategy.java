package edu.rice.comp504.model.strategy.interactStrategy;

import edu.rice.comp504.model.paint.Ball;

import java.awt.*;

/**
 * Simulates an elastic collision; transfers force from an interaction to both balls.
 */
public class BilliardStrategy implements IInteractStrategy {

    private Point force;
    private InteractStrategyUnwrapper my_int_unwrapper;

    /**
     * Constructor
     */
    public BilliardStrategy() {
        force = new Point(0,0);
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
        this.force = new Point((int)(this.force.getX() + addx), (int)(this.force.getY() + addy));
    }

    /**
     * Gives the stored velocity change.
     * @return force
     */
    public Point getForce() {return this.force;}

    public void zeroForce() {this.force = new Point(0,0);}

    /**
     * The billiard ball (src) will accelerate itself and the dest ball as though the dest ball
     * were at rest; if the dest ball later calls this method on the src ball, the two effects will
     * combine to make a true elastic collision.
     *
     * * @param src  The src ball will impose the interaction strategy on the dest ball.
     * @param dest The dest ball behavior will be affected by the src ball interaction strategy
     */
    public void interact(Ball src, Ball dest) {

        //we project the velocity vector of the src ball onto the vector from the src center to the dest center.
        //this is the force we apply to dest, and in reverse, to src.

        double force_vector_x = dest.getLocation().getX() - src.getLocation().getX();
        double force_vector_y = dest.getLocation().getY() - src.getLocation().getY();
        double dot_product_for_proj = src.getVelocity().getX() * force_vector_x + src.getVelocity().getY() * force_vector_y;
        force_vector_x = force_vector_x * dot_product_for_proj / Math.pow((src.getRadius() + dest.getRadius()), 2);
        force_vector_y = force_vector_y * dot_product_for_proj / Math.pow((src.getRadius() + dest.getRadius()), 2);

        for (int i = 0; i < 2; i++) {
            int a;
            Ball b;
            if (i == 0) {
                a = -1;
                b = src;
            } else {
                a = 1;
                b = dest;
            }
            if (b.getInteractStrategy().getName().contains("billiard")) {
                BilliardStrategy strat = (BilliardStrategy)
                        my_int_unwrapper.getBaseInteractStrategy(b, "billiard");
                strat.addForce(a * force_vector_x, a * force_vector_y);

            } else b.setVelocity(new Point((int) (b.getVelocity().getX() + force_vector_x),
                    (int) (b.getVelocity().getY() + force_vector_y)));
        }

    }


}
