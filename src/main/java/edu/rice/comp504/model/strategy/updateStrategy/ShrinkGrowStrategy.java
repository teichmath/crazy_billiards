package edu.rice.comp504.model.strategy.updateStrategy;

import edu.rice.comp504.model.paint.Ball;

/**
 * Ball continuously grows and shrinks between half and twice its original radius.
 * Direction switches randomly every 0.5–5 seconds, and immediately at the boundaries.
 * Rate = 0.3 * originalRadius per second (0.03r per frame at 10 FPS).
 */
public class ShrinkGrowStrategy implements IUpdateStrategy {

    private boolean initialized = false;
    private double minRadius;
    private double maxRadius;
    private double ratePerFrame;
    private double currentRadius;
    private boolean growing;
    private int framesUntilSwitch;

    public ShrinkGrowStrategy() {
        growing = Math.random() < 0.5;
        framesUntilSwitch = randomSwitchFrames();
    }

    public String getName() { return "shrinkgrow"; }

    private void initialize(Ball context) {
        double r = context.getRadius();
        minRadius = r / 2.0;
        maxRadius = r * 2.0;
        ratePerFrame = 0.03 * r;
        currentRadius = r;
        initialized = true;
    }

    private int randomSwitchFrames() {
        // 0.5s = 5 frames, 5s = 50 frames at 10 FPS
        return (int)(Math.random() * 46) + 5;
    }

    public void updateState(Ball context) {
        if (!initialized) initialize(context);

        context.nextLocation((int) context.getVelocity().getX(), (int) context.getVelocity().getY());

        if (growing) {
            currentRadius += ratePerFrame;
            if (currentRadius >= maxRadius) {
                currentRadius = maxRadius;
                growing = false;
                framesUntilSwitch = randomSwitchFrames();
            } else {
                framesUntilSwitch--;
                if (framesUntilSwitch <= 0) {
                    growing = false;
                    framesUntilSwitch = randomSwitchFrames();
                }
            }
        } else {
            currentRadius -= ratePerFrame;
            if (currentRadius <= minRadius) {
                currentRadius = minRadius;
                growing = true;
                framesUntilSwitch = randomSwitchFrames();
            } else {
                framesUntilSwitch--;
                if (framesUntilSwitch <= 0) {
                    growing = true;
                    framesUntilSwitch = randomSwitchFrames();
                }
            }
        }

        context.setRadius((int) Math.round(currentRadius));
    }
}
