package edu.rice.comp504.model.strategy.interactStrategy;

import edu.rice.comp504.model.paint.Ball;
import edu.rice.comp504.model.strategy.updateStrategy.EatenWrapperUpdateStrategy;
import edu.rice.comp504.model.strategy.updateStrategy.UpdateStrategyUnwrapper;

import java.awt.*;

/**
 * The ball with this strategy will be a transparent ball that grows as it eats other balls, which will be drawn inside it.
 */
public class BlobStrategy implements IInteractStrategy {

    private int number_of_eatens;
    private double eaten_max_absval_x;
    private double eaten_max_absval_y;
    private InteractStrategyUnwrapper my_int_unwrapper;
    private UpdateStrategyUnwrapper my_up_unwrapper;
    //private EatensKeeper my_eatens_keeper;

    /**
     * Constructor
     */
    public BlobStrategy() {
        eaten_max_absval_x = 0;
        eaten_max_absval_y = 0;
        my_int_unwrapper = InteractStrategyUnwrapper.makeUnwrapper();
     //   my_eatens_keeper = new EatensKeeper();
    }

    /**
     * Get the interaction strategy name.
     * @return The interaction strategy name.
     */
    public String getName() {return "blob";}



    /**
     * Sets up destination balls to be drawn as figures inside the source ball; expands source ball as necessary.
     *
     * * @param src  The src ball will impose the interaction strategy on the dest ball.
     * @param dest The dest ball behavior will be affected by the src ball interaction strategy
     */
    public void interact(Ball src, Ball dest) {

        src.setColor("rgba(0, 0, 200, 0)");

        String dest_strat = dest.getInteractStrategy().getName();
        if(!dest_strat.contains("override_interact")) {
            if (dest_strat.contains("billiard")) {
                my_int_unwrapper.getBaseInteractStrategy(dest, "billiard").interact(dest, src);
            }
            if (dest_strat.contains("swapper")) {
                my_int_unwrapper.getBaseInteractStrategy(dest, "swapper").interact(dest, src);
            }
            if (dest_strat.contains("smasher")) {
                SmasherStrategy my_smash = (SmasherStrategy)
                        my_int_unwrapper.getBaseInteractStrategy(dest, "smasher");
                src.setColor("black");
                my_smash.interact(dest, src);
                //for (Ball b : my_eatens_keeper.getEatens()) my_smash.interact(dest, b);
                //my_eatens_keeper.clearEatens();
            }
            if (dest_strat.contains("splitter")) {
                my_int_unwrapper.getBaseInteractStrategy(dest, "splitter").interact(dest, src);
            }
//          if (dest_strat.contains("blob")) {
//                Ball top_blob = src;
//                Ball sous_blob = dest;
//                if(dest.getRadius() > src.getRadius()) {
//                    top_blob = dest;
//                    sous_blob = src;
//                }
//                BlobStrategy sous_blob_strat = (BlobStrategy) my_int_unwrapper.getBaseInteractStrategy(sous_blob, "blob");
//                for(Ball sous_eaten: sous_blob_strat.getEatens()) {
//                    if(sous_eaten.getUpdateStrategy().getName().contains("eatenwrapper_update")) my_up_unwrapper.removeWrapper(sous_eaten, "eatenwrapper_update");
//                    addEaten(top_blob, sous_eaten);
//                }
//                //is the below enough- this is a "cast" version of the strategy we want to affect. do we need to set the int strat of sous blob here?
//                sous_blob_strat.clearEatens();
//                addEaten(top_blob, sous_blob);
//          }
        }
//
        if(!(dest.getInteractStrategy().getName().contains("smasher") && !dest.getInteractStrategy().getName().contains("override"))
            && !dest.getInteractStrategy().getName().contains("blob")) {
            dest.setInteractStrategy(new OverrideInteractStrategy(dest.getInteractStrategy()));
            dest.setRadius((int)(dest.getRadius()/2));
            addEaten(src, dest);
        }

        //this.eatens.add(dest);
        //my_eatens_keeper.writeEaten(dest);

//
    }

    /**
     * Helper that chooses a relative position for the eaten ball.
     * @param a
     * @param b
     */
    public void addEaten(Ball a, Ball b) {
        //find a relative position inside the blob for the new follower
        Point new_relative_pos = new Point(0,0);
        if(number_of_eatens > 0) {
//            Ball random_eaten = eatens.get((int)(Math.random()*(eatens.size() - 1)));
//            double angle = Math.random()*2*3.14159;
//            EatenWrapperUpdateStrategy existing_strategy = (EatenWrapperUpdateStrategy) random_eaten.getUpdateStrategy();
//            new_relative_pos = new Point((int)(existing_strategy.getRelativePos().getX()
//                    + (b.getRadius() + random_eaten.getRadius())*Math.cos(angle)),
//                    (int)(existing_strategy.getRelativePos().getY()
//                    + (b.getRadius() + random_eaten.getRadius())*Math.sin(angle)));
            new_relative_pos = new Point((int)(10*number_of_eatens*Math.cos(.1 + number_of_eatens)),
                    (int)(10*number_of_eatens*Math.sin(.1 + number_of_eatens)));

        }
        b.setUpdateStrategy(new EatenWrapperUpdateStrategy(b.getUpdateStrategy(), a, new_relative_pos));
        this.number_of_eatens++;

        eaten_max_absval_x = Math.max(Math.abs(new_relative_pos.getX() + b.getRadius()), eaten_max_absval_x);
        eaten_max_absval_x = Math.max(Math.abs(new_relative_pos.getX() - b.getRadius()), eaten_max_absval_x);
        eaten_max_absval_y = Math.max(Math.abs(new_relative_pos.getY() + b.getRadius()), eaten_max_absval_y);
        eaten_max_absval_y = Math.max(Math.abs(new_relative_pos.getY() - b.getRadius()), eaten_max_absval_y);
        double new_radius = 1.415 * Math.max(eaten_max_absval_x, eaten_max_absval_y);
       // double box_hypotenuse = Math.sqrt(Math.pow(2*eaten_max_absval_x, 2)
//                    + Math.pow(2*eaten_max_absval_y, 2));
        if(new_radius > a.getRadius()) a.setRadius((int)new_radius);

    }

}

