package edu.rice.comp504.model.strategy.updateStrategy;

import edu.rice.comp504.model.paint.Ball;

/**
 * the null strategy among the update strategies.
 */
public class NullUpdateStrategy implements IUpdateStrategy {
    private static IUpdateStrategy my_iupdate;

    /**
     * Constructor
     */
    private NullUpdateStrategy() {

    }

    /**
     * makeStrategy allows us to create a singleton of this class.
     * @return the singleton.
     */
    public static IUpdateStrategy makeStrategy() {
        if(my_iupdate == null) {
            my_iupdate = new NullUpdateStrategy();
        }
        return my_iupdate;
    }

    /**
     * get the strategy name
     * @return the name
     */
    @Override
    public String getName() {
        return "null_update";
    }

    /**
     * Do nothing- except making sure the color is black.
      * @param context The ball to update.
     */
    @Override
    public void updateState(Ball context) {
        context.setColor("black");
   }
}
