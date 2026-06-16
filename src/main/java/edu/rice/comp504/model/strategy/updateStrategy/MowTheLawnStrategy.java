package edu.rice.comp504.model.strategy.updateStrategy;

import edu.rice.comp504.model.paint.Ball;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Ball sweeps back and forth across the canvas in a lawn-mowing pattern, like Space Invaders aliens.
 * Orientation (horizontal or vertical sweep) is chosen randomly at creation.
 * External ball collisions push it off course; the strategy corrects it back toward its current row.
 * After completing the full pattern it reverses and mows back the other way.
 */
public class MowTheLawnStrategy implements IUpdateStrategy {

    private final Point dims;
    private final boolean horizontal; // true = sweeps along x, advances along y
    private final double sweepSpeed;

    private boolean initialized = false;
    private int sweepDir;      // 1 or -1
    private int advanceDir;    // 1 or -1 (direction of row advancement)
    private double rowSpacing;
    private double targetAdvancePos;
    private int lastSetSweepVelSign;

    private int knockedFrames = 0;
    private static final int KNOCKED_DURATION = 30;  // ~3 s at 100 ms tick

    public MowTheLawnStrategy(Point dims) {
        this.dims = dims;
        this.horizontal = Math.random() < 0.5;
        this.sweepDir = Math.random() < 0.5 ? 1 : -1;
        this.advanceDir = 1;
        this.lastSetSweepVelSign = sweepDir;
        this.sweepSpeed = 3.0 + Math.random() * 5.0;
    }

    public String getName() { return "mowthelawn"; }

    private void initialize(Ball context) {
        rowSpacing = context.getRadius() * 2.5;
        targetAdvancePos = horizontal ? context.getLocation().getY() : context.getLocation().getX();
        initialized = true;
    }

    public void updateState(Ball context) {
        if (!initialized) initialize(context);

        double vx = context.getVelocity().getX();
        double vy = context.getVelocity().getY();
        double speed = Math.sqrt(vx * vx + vy * vy);

        // Detect external cue-stick knock: speed well above what the strategy would set
        if (speed > sweepSpeed * 2.5 + 10) {
            knockedFrames = KNOCKED_DURATION;
        }

        if (knockedFrames > 0) {
            // Decay velocity (temporary friction) so ball eventually slows to lawn-mowing speed
            vx *= 0.93;
            vy *= 0.93;

            // Gentle correction back toward the target row on the advance axis
            double advancePos = horizontal ? context.getLocation().getY() : context.getLocation().getX();
            double advanceDiff = targetAdvancePos - advancePos;
            double correction = Math.max(-3, Math.min(3, advanceDiff * 0.15));
            if (horizontal) vy += correction;
            else            vx += correction;

            context.setVelocity(new Point2D.Double(vx, vy));

            // Keep sweepDir in sync so resume is clean
            double sweepV = horizontal ? vx : vy;
            if (Math.abs(sweepV) > 0.5) sweepDir = (int) Math.signum(sweepV);
            lastSetSweepVelSign = sweepDir;

            knockedFrames--;
            context.nextLocation(context.getVelocity().getX(), context.getVelocity().getY());
            return;
        }

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
            context.setVelocity(new Point2D.Double(sweepVel, advanceVel));
        } else {
            context.setVelocity(new Point2D.Double(advanceVel, sweepVel));
        }

        lastSetSweepVelSign = sweepDir;
        context.nextLocation(context.getVelocity().getX(), context.getVelocity().getY());
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
