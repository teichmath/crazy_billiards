package edu.rice.comp504.model.strategy.updateStrategy;


import edu.rice.comp504.model.paint.Ball;

/**
 * The rotate strategy will cause the ball to rotate around a fixed point; the angle
 * of rotation may mean that the full arc of motion carries outside the canvas.
 */
public class RotateStrategy implements IUpdateStrategy {

    private static IUpdateStrategy my_iupdate;

    /**
     * Constructor
     */
    private RotateStrategy() {

    }

    /**
     * makeStrategy allows us to create a singleton of this class.
     * @return the singleton.
     */
    public static IUpdateStrategy makeStrategy() {
        if(my_iupdate == null) {
            my_iupdate = new RotateStrategy();
        }
        return my_iupdate;
    }

    /**
     * Get the strategy name
     * @return strategy name
     */
    public String getName() {
        return "rotate";
   }

    /**
     * Update the ball state in the ball world: rotate the ball using a ball method.
     * @param context The ball to update
     */
    public void updateState(Ball context) {
        context.rotate(.2);
        context.nextLocation((int)(context.getVelocity().getX()), (int)(context.getVelocity().getY()));
    }
}
