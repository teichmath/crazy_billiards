package edu.rice.comp504.model.strategy.interactStrategy;

import edu.rice.comp504.model.paint.Ball;
import edu.rice.comp504.model.strategy.updateStrategy.StraightToHellStrategy;

import java.awt.*;
import java.util.LinkedList;


/**
 * Smashes balls it hits; its victims are eventually removed from the ball world.
 */
public class SmasherStrategy implements IInteractStrategy {

    private LinkedList<Ball> resulting_balls;

    /**
     * Constructor
     */
    public SmasherStrategy() {
        resulting_balls = new LinkedList<Ball>();
    }

    /**
     * Get the interaction strategy name.
     * @return The interaction strategy name.
     */
    public String getName() {return "smasher";}


    /**
     * wrap the dest ball so that it essentially disappears. In its place, create many tiny balls that have a brief
     * shelf life (they represent the bits of the explosion).
     *
     * * @param src  The src ball will impose the interaction strategy on the dest ball.
     * @param dest The dest ball behavior will be affected by the src ball interaction strategy
     */
    public void interact(Ball src, Ball dest) {

        dest.setUpdateStrategy(new StraightToHellStrategy(2));

        //we create many balls from the dest ball. They will become children of the src ball.
System.out.println("hi from smasher 1");
        double destx = dest.getLocation().getX();
        double desty = dest.getLocation().getY();
        double destvelx = dest.getVelocity().getX();
        double destvely = dest.getVelocity().getY();
        System.out.println("hi from smasher 2");

        this.resulting_balls.add(new Ball(dest.getLocation(), 2, dest.getVelocity(), dest.getColor(),
                new StraightToHellStrategy(6), new OverrideInteractStrategy(dest.getInteractStrategy())));

        int smash_bit_rounds = (int) (Math.floor(dest.getRadius() / 10));
        System.out.println("hi from smasher 3");

        for (int b = 0; b < smash_bit_rounds; b++) {
            for (int i = 0; i < 6; i++) {
                double angle = 3.14159 / 3 * i + 0.5 * b;
                double vector_x = (b + 1) * 10 * Math.cos(angle);
                double vector_y = (b + 1) * 10 * Math.sin(angle);
                Point new_loc = new Point((int) (destx + vector_x), (int) (desty + vector_y));
                Point new_vel = new Point((int) (2 * destvelx + 0.5 * vector_x), (int) (2 * destvely + 0.5 * vector_y));
                this.resulting_balls.add(new Ball(new_loc, 2, new_vel, dest.getColor(),
                        new StraightToHellStrategy(6),
                        new OverrideInteractStrategy(dest.getInteractStrategy())));
            }
        }
        System.out.println("hi from smasher 4");

        System.out.println(dest.getInteractStrategy().getName() + " smash65");
        if (!dest.getInteractStrategy().getName().contains("persistencelimit_interact"))
            dest.setInteractStrategy(new PersistenceLimitWrapperInteractStrategy(dest.getInteractStrategy()));

    }

    /**
     * get the list of balls that we're adding (the explosion bits)
     * @return the list of bits
     */
    public LinkedList<Ball> getResultingBalls() {
        return this.resulting_balls;
    }

    /**
     * clear the list of explosion bits so we can have another explosion.
     */
    public void clearResultingBalls() {this.resulting_balls.clear();}

}
