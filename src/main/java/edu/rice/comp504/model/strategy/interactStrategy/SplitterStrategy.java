package edu.rice.comp504.model.strategy.interactStrategy;

import edu.rice.comp504.model.paint.Ball;
import edu.rice.comp504.model.strategy.updateStrategy.StraightToHellStrategy;
import edu.rice.comp504.model.strategy.updateStrategy.UpdateStrategyFactory;

import java.awt.*;
import java.util.LinkedList;


/**
 * Cut the ball we hit in half; each half is a new independent ball.
 */
public class SplitterStrategy implements IInteractStrategy {

    private LinkedList<Ball> resulting_balls;
    private InteractStrategyFactory my_intfact;
    private UpdateStrategyFactory my_upfact;


    /**
     * Constructor
     */
    public SplitterStrategy() {

        resulting_balls = new LinkedList<Ball>();
        my_intfact = InteractStrategyFactory.makeFactory();
        my_upfact = UpdateStrategyFactory.makeFactory();
    }

    /**
     * Get the interaction strategy name.
     * @return The interaction strategy name.
     */
    public String getName() {return "splitter";}


    /**
     *  Like the smasher strategy, this one terminates the dest ball and replaces it, but this time there are
     *  only two new balls created and they persist as copies of the original dest.
     *
     * * @param src  The src ball will impose the interaction strategy on the dest ball.
     * @param dest The dest ball behavior will be affected by the src ball interaction strategy
     */
    public void interact(Ball src, Ball dest) {

        double destx = dest.getLocation().getX();
        double desty = dest.getLocation().getY();
        double destvelx = dest.getVelocity().getX();
        double destvely = dest.getVelocity().getY();
        double dest_radius = dest.getRadius();
        double destvel_mag = Math.sqrt(Math.pow(destvelx, 2) + Math.pow(destvely, 2));

        if(!dest.getInteractStrategy().getName().contains("blob")) {
            for (int c = 0; c < 2; c++) {
                int a = -1;
                if (c == 0) a = 1;

                double new_pupa_loc_x = 0;
                double new_pupa_loc_y = 0;

                if(destvel_mag != 0) {
                    new_pupa_loc_x = destx + a * destvely / destvel_mag * dest_radius * 0.5;
                    new_pupa_loc_y = desty - a * destvelx / destvel_mag * dest_radius * 0.5;
                }
                else {
                    new_pupa_loc_x = destx + a * 10;
                    new_pupa_loc_y = desty - a * 10;
                }

                double new_pupa_vel_x = destvelx + a * destvely * 0.5;
                double new_pupa_vel_y = destvely - a * destvelx * 0.5;

                this.resulting_balls.add(new Ball(new Point((int) new_pupa_loc_x, (int) new_pupa_loc_y),
                        (int) (dest_radius * 0.4), new Point((int) new_pupa_vel_x, (int) new_pupa_vel_y), dest.getColor(),
                        my_upfact.makeStrategy(dest.getUpdateStrategy().getName()),
                        my_intfact.makeStrategy(dest.getInteractStrategy().getName())));
            }

            dest.setUpdateStrategy(new StraightToHellStrategy(2));

            System.out.println(dest.getInteractStrategy().getName() + " split67");
            if (!dest.getInteractStrategy().getName().contains("persistencelimit_interact"))
                dest.setInteractStrategy(new PersistenceLimitWrapperInteractStrategy(dest.getInteractStrategy()));
        }
        else {
            double vel_mag = 1.25*Math.sqrt((Math.pow(destvelx, 2) + Math.pow(destvely, 2) )/ 2);
            this.resulting_balls.add( new Ball( new Point((int)(destx + 1.1*dest_radius),
                    (int)(desty + 1.1*dest_radius)), (int)(dest_radius * 0.4), new Point((int)vel_mag, (int)vel_mag),
                    dest.getColor(), my_upfact.makeStrategy(dest.getUpdateStrategy().getName()),
                    my_intfact.makeStrategy("blob")));
        }

    }

    /**
     * get the list of balls that we're adding (the two halves)
     * @return the list of two balls
     */
    public LinkedList<Ball> getResultingBalls() {
        return this.resulting_balls;
    }

    /**
     * clear the list of balls so we can split another ball
     */
    public void clearResultingBalls() {this.resulting_balls.clear();}

}
