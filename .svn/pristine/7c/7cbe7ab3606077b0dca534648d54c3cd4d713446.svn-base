package edu.rice.comp504.model.strategy.updateStrategy;


import edu.rice.comp504.model.paint.Ball;

/**
 * The color change strategy will update the ball's color with random values.
 */
public class ColorChangeStrategy implements IUpdateStrategy {

    private static IUpdateStrategy my_iupdate;

    /**
     * Constructor
     */
    private ColorChangeStrategy() {

    }

    /**
     * makeStrategy allows us to create a singleton of this class.
     * @return the singleton.
     */
    public static IUpdateStrategy makeStrategy() {
        if(my_iupdate == null) {
            my_iupdate = new ColorChangeStrategy();
        }
        return my_iupdate;
    }

    /**
     * Get the strategy name
     * @return strategy name
     */
    public String getName() {
        return "colorchange";
    }

    /**
     * Update the ball state in the ball world
     * @param context The ball to update
     */
    public void updateState(Ball context) {
        if(Math.random() > .08) context.setColor("rgb(" + getRnd(0, 255) + "," + getRnd(0, 255)+ "," +getRnd(0, 255)+")");
    }

    /**
     * Gives a random integer based on specifications.
     * @param base
     * @param limit
     * @return random integer
     */
    private int getRnd(int base, int limit) {
        return (int)Math.floor(Math.random() * limit + base);
    }

}
