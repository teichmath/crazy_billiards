package edu.rice.comp504.model.strategy.updateStrategy;

import edu.rice.comp504.model.paint.Ball;

import java.awt.*;

/**
 * Ball sweeps back and forth across the canvas in a lawn-mowing pattern, like Space Invaders aliens.
 * Orientation (horizontal or vertical sweep) is chosen randomly at creation.
 * External ball collisions push it off course; the strategy corrects it back toward its current row.
 * After completing the full pattern it reverses and mows back the other way.
 */
public class MowTheLawnStrategy implements IUpdateStrategy {

    private final Point dims;
    private final boolean horizontal; // true = sweeps along x, advances along y
    private final double sweepSpeed = 4.0;

    private boolean initialized = false;
    private int sweepDir;      // 1 or -1
    private int advanceDir;    // 1 or -1 (direction of row advancement)
    private double rowSpacing;
    private double targetAdvancePos;
    private int lastSetSweepVelSign;

    public MowTheLawnStrategy(Point dims) {
        this.dims = dims;
        this.horizontal = Math.random() < 0.5;
        this.sweepDir = Math.random() < 0.5 ? 1 : -1;
        this.advanceDir = 1;
        this.lastSetSweepVelSign = sweepDir;
    }

    public String getName() { return "mowthelawn"; }

    private void initialize(Ball context) {
        rowSpacing = context.getRadius() * 2.5;
        targetAdvancePos = horizontal ? context.getLocation().getY() : context.getLocation().getX();
        initialized = true;
    }

    public void updateState(Ball context) {
        if (!initialized) initialize(context);

        int ctxSweepVel = horizontal ? (int) context.getVelocity().getX() : (int) context.getVelocity().getY();

        // Detect wall bounce on sweep axis: velocity sign flipped since last frame AND ball near wall
        if (ctxSweepVel != 0 && (int) Math.signum(ctxSweepVel) != lastSetSweepVelSign) {
            double sweepPos = horizontal ? context.getLocation().getX() : context.getLocation().getY();
            double sweepMax = horizontal ? dims.getX() : dims.getY();
            boolean nearWall = sweepPos <= context.getRadius() + sweepSpeed + 2
                    || sweepPos >= sweepMax - context.getRadius() - sweepSpeed - 2;
            if (nearWall) {
                advanceRow(context);
            }
            sweepDir = (int) Math.signum(ctxSweepVel);
        }

        // Compute correction toward current target row in advance axis
        double advancePos = horizontal ? context.getLocation().getY() : context.getLocation().getX();
        double advanceDiff = targetAdvancePos - advancePos;
        int advanceVel = (int) Math.round(advanceDiff * 0.35);
        advanceVel = Math.max(-8, Math.min(8, advanceVel));

        int sweepVel = (int) (sweepSpeed * sweepDir);

        if (horizontal) {
            context.setVelocity(new Point(sweepVel, advanceVel));
        } else {
            context.setVelocity(new Point(advanceVel, sweepVel));
        }

        lastSetSweepVelSign = sweepDir;
        context.nextLocation((int) context.getVelocity().getX(), (int) context.getVelocity().getY());
    }

    private void advanceRow(Ball context) {
        targetAdvancePos += advanceDir * rowSpacing;
        double advanceDim = horizontal ? dims.getY() : dims.getX();
        double minPos = context.getRadius();
        double maxPos = advanceDim - context.getRadius();

        if (targetAdvancePos > maxPos) {
            targetAdvancePos = maxPos;
            advanceDir = -1;
        } else if (targetAdvancePos < minPos) {
            targetAdvancePos = minPos;
            advanceDir = 1;
        }
    }
}
