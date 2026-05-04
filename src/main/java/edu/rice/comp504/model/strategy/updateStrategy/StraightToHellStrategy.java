package edu.rice.comp504.model.strategy.updateStrategy;


import edu.rice.comp504.model.paint.Ball;

/**
 * Like the straight strategy, but it carries a life clock, which causes the radius to go to zero eventually, at
 * which point the ball will be deleted from the ball world.
 */
public class StraightToHellStrategy implements IUpdateStrategy {

    private int life_clock;

    /**
     * Constructor
     */
    public StraightToHellStrategy(int life_clock) {
        this.life_clock = life_clock;
    }

    /**
     * Get the strategy name
     * @return strategy name
     */
    public String getName() {
        return "straighttohell";
    }

    /**
     * Update the ball state in the ball world: simply apply the velocities. Set radius to zero when the time comes.
     * @param context The ball to update
     */
    public void updateState(Ball context) {
        context.nextLocation((int)(context.getVelocity().getX()), (int)(context.getVelocity().getY()));
        life_clock--;
        if(life_clock <= 0) context.setRadius(0);
    }
}
