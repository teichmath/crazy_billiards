package edu.rice.comp504.model.cmd;

import edu.rice.comp504.model.DispatchAdapter;
import edu.rice.comp504.model.PhysicsConfig;
import edu.rice.comp504.model.paint.Ball;
import edu.rice.comp504.model.strategy.interactStrategy.*;
import edu.rice.comp504.model.strategy.updateStrategy.FollowerWrapperUpdateStrategy;
import edu.rice.comp504.model.strategy.interactStrategy.RepelStrategy;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * The UpdateCommand orchestrates interactions between balls and walls and between balls and balls, and the updating of
 * ball locations due to update strategies. See readme for details.
 */
public class UpdateCommand implements IBallCmd {

    private static final double MAX_SPEED = 300.0;

    private Point dims;
    private LinkedList<Ball> ball_group = new LinkedList<>();
    private boolean update_complete;
    private DispatchAdapter dad;
    private int group_size;
    private InteractStrategyUnwrapper my_int_unwrapper;

    public UpdateCommand(Point dims, DispatchAdapter dad, int group_size) {
        this.dims = dims;
        this.dad = dad;
        this.update_complete = false;
        this.group_size = group_size;
        this.my_int_unwrapper = InteractStrategyUnwrapper.makeUnwrapper();
    }

    public void execute(Ball context) {
        System.out.println("execute hello 1");
        boolean interacted = false;
        context.wallCollision(dims);
        if (!context.getInteractStrategy().getName().contains("override_interact")) {
otherloop:  for (Ball other : ball_group) {
                if (!other.getInteractStrategy().getName().contains("override_interact")) {
                    System.out.println("execute hello 2");

                    for (int i = 0; i < 2; i++) {
                        Ball a = context;
                        Ball b = other;
                        System.out.println("execute hello 3");
                        if (i == 1) {
                            a = other;
                            b = context;
                        }
                        if (a.getUpdateStrategy().getName().startsWith("follower")) {
                            FollowerWrapperUpdateStrategy fwstrat = (FollowerWrapperUpdateStrategy) a.getUpdateStrategy();
                            if (fwstrat.getLeader() == b) continue otherloop;
                        }
                    }
                    System.out.println("execute hello 4");

                    Point2D.Double record_context_vel = context.getVelocity();
                    Point2D.Double record_other_vel = other.getVelocity();

                    if (context.ballCollision(other, dad)) {
                        interacted = true;
                        other.ballCollision(context, dad);
                        System.out.println("execute hello 5");

                        // Blobs rely on BilliardStrategy.interact() for their velocity update,
                        // so restore pre-collision velocities only when a blob is involved.
                        // For regular billiard balls, ballCollision() is the sole physics source.
                        boolean ctxBlob = context.getInteractStrategy().getName().contains("blob");
                        boolean othBlob = other.getInteractStrategy().getName().contains("blob");
                        if ((ctxBlob || othBlob) && context.getInteractStrategy().getName().contains("billiard"))
                            context.setVelocity(record_context_vel);
                        if ((ctxBlob || othBlob) && other.getInteractStrategy().getName().contains("billiard"))
                            other.setVelocity(record_other_vel);
                        System.out.println("execute hello 6");

                        Ball a = context;
                        Ball b = other;
                        if (other.getInteractStrategy().getName().contains("swapper") && !context.getInteractStrategy().getName().contains("swapper")) {
                            a = (b == context) ? context : other;
                            b = (a == context) ? other : context;
                        }
                        if (other.getInteractStrategy().getName().contains("blob") && !context.getInteractStrategy().getName().contains("blob")) {
                            a = (b == context) ? context : other;
                            b = (a == context) ? other : context;
                        }
                        System.out.println("execute hello 7");
                        a.getInteractStrategy().interact(a, b);
                        System.out.println("execute hello 8");
                        b.getInteractStrategy().interact(b, a);
                        System.out.println("execute hello 9");

                        for (int i = 0; i < 2; i++) {
                            if (i == 0) b = context;
                            else b = other;
                            if (b.getInteractStrategy().getName().contains("smasher")) {
                                SmasherStrategy smstr = (SmasherStrategy)
                                        my_int_unwrapper.getBaseInteractStrategy(b, "smasher");
                                for (Ball ball : smstr.getResultingBalls()) dad.addObserver(ball);
                                smstr.clearResultingBalls();
                            } else if (b.getInteractStrategy().getName().contains("splitter")) {
                                SplitterStrategy spstr = (SplitterStrategy)
                                        my_int_unwrapper.getBaseInteractStrategy(b, "splitter");
                                for (Ball ball : spstr.getResultingBalls()) dad.addObserver(ball);
                                spstr.clearResultingBalls();
                                System.out.println("execute hello 10");
                            }
                        }
                    }
                }
            }
        }
        System.out.println("execute hello 11");

        ball_group.add(context);
        System.out.println("execute hello 12");

        if (ball_group.size() == group_size && !update_complete) {

            boolean[] updated = new boolean[ball_group.size()];
            Arrays.fill(updated, false);

            System.out.println("execute hello 13");
            capSpeeds(ball_group);
            for (int i = 0; i < ball_group.size(); i++) {
                Ball ball = ball_group.get(i);
                if (ball.getInteractStrategy().getName().contains("blob")) {
                    if (ball.getInteractStrategy().getName().contains("billiard")) {
                        BilliardStrategy bstr = (BilliardStrategy)
                                my_int_unwrapper.getBaseInteractStrategy(ball, "billiard");
                        ball.setVelocity(new Point2D.Double(
                                ball.getVelocity().getX() + bstr.getForceX(),
                                ball.getVelocity().getY() + bstr.getForceY()));
                        bstr.zeroForce();
                    }
                    applyPhysics(ball);
                    System.out.println("execute hello 14");
                    ball.getUpdateStrategy().updateState(ball);
                    updated[i] = true;
                    System.out.println("execute hello 15");
                }
            }

            if (dad.break_it > 0) {
                System.out.println("breakpoint here");
            }

            if (interacted) {
                dad.break_it++;
            }

            for (int i = 0; i < ball_group.size(); i++) {
                Ball bi = ball_group.get(i);
                if (bi.getInteractStrategy().getName().contains("repel")) {
                    IInteractStrategy repelBase = my_int_unwrapper.getBaseInteractStrategy(bi, "repel");
                    if (repelBase instanceof RepelStrategy) {
                        double influenceR = RepelStrategy.influenceRadius(bi.getRadius());
                        for (int j = 0; j < ball_group.size(); j++) {
                            if (i == j) continue;
                            Ball bj = ball_group.get(j);
                            double dist = Math.sqrt(
                                    Math.pow(bi.getLocation().getX() - bj.getLocation().getX(), 2)
                                  + Math.pow(bi.getLocation().getY() - bj.getLocation().getY(), 2));
                            if (dist > bi.getRadius() + bj.getRadius() && dist <= influenceR) {
                                repelBase.interact(bi, bj);
                            }
                        }
                    }
                }
            }

            for (int i = 0; i < ball_group.size(); i++) {
                if (!updated[i]) {
                    Ball ball = ball_group.get(i);
                    System.out.println("execute hello 16");
                    if (ball.getInteractStrategy().getName().contains("billiard")) {
                        BilliardStrategy bstr = (BilliardStrategy)
                                my_int_unwrapper.getBaseInteractStrategy(ball, "billiard");
                        System.out.println("execute hello 17");
                        ball.setVelocity(new Point2D.Double(
                                ball.getVelocity().getX() + bstr.getForceX(),
                                ball.getVelocity().getY() + bstr.getForceY()));
                        bstr.zeroForce();
                    }
                    applyPhysics(ball);
                    ball.getUpdateStrategy().updateState(ball);
                    System.out.println("execute hello 18");
                }
            }

            for (int i = 0; i < ball_group.size(); i++) {
                Ball bi = ball_group.get(i);
                if (bi.getInteractStrategy().getName().contains("override")) continue;
                for (int j = i + 1; j < ball_group.size(); j++) {
                    Ball bj = ball_group.get(j);
                    if (bj.getInteractStrategy().getName().contains("override")) continue;

                    double dx = bi.getLocation().getX() - bj.getLocation().getX();
                    double dy = bi.getLocation().getY() - bj.getLocation().getY();
                    double distance = Math.sqrt(dx * dx + dy * dy);
                    double overlap = bi.getRadius() + bj.getRadius() - distance;
                    if (overlap <= 0) continue;

                    if (distance < 0.5) { dx = 1; dy = 0; distance = 1; }
                    double nx = dx / distance;
                    double ny = dy / distance;
                    double half = overlap / 2.0;

                    boolean bi_blocked = wouldExceedBounds(bi, nx * half, ny * half);
                    boolean bj_blocked = wouldExceedBounds(bj, -nx * half, -ny * half);
                    double bi_push = bi_blocked ? 0 : (bj_blocked ? overlap : half);
                    double bj_push = bj_blocked ? 0 : (bi_blocked ? overlap : half);

                    if (bi_push > 0) bi.nextLocation(nx * bi_push, ny * bi_push);
                    if (bj_push > 0) bj.nextLocation(-nx * bj_push, -ny * bj_push);

                    if (bi_push > 0) {
                        double vi_n = bi.getVelocity().getX() * nx + bi.getVelocity().getY() * ny;
                        if (vi_n < 0) bi.setVelocity(new Point2D.Double(
                                bi.getVelocity().getX() - nx * vi_n,
                                bi.getVelocity().getY() - ny * vi_n));
                    }
                    if (bj_push > 0) {
                        double vj_n = bj.getVelocity().getX() * nx + bj.getVelocity().getY() * ny;
                        if (vj_n > 0) bj.setVelocity(new Point2D.Double(
                                bj.getVelocity().getX() - nx * vj_n,
                                bj.getVelocity().getY() - ny * vj_n));
                    }
                }
            }

            capSpeeds(ball_group);

            for (int i = 0; i < ball_group.size(); i++) {
                System.out.println("execute hello 20");
                Ball bi = ball_group.get(i);
                boolean outcome = bi.wallCollision(dims);
                if (outcome) System.out.println("ball " + i + " hits! loc: " + bi.getLocation().getX() + ", " + bi.getLocation().getY());
                System.out.println("execute hello 21");
            }

            update_complete = true;
        }
    }

    /**
     * Applies sliding→rolling transition and side-spin decay.
     * Reads all constants live from PhysicsConfig so slider changes take effect immediately.
     * Only runs for balls that opted into friction (frictionFactor < 1.0).
     */
    private void applyPhysics(Ball ball) {
        if (ball.getFrictionFactor() >= 1.0) return;

        PhysicsConfig cfg = PhysicsConfig.get();
        double G_EFF = cfg.gEff, MU_K = cfg.muK, MU_R = cfg.muR, MU_S = cfg.muS;

        double vx = ball.getVelocity().getX();
        double vy = ball.getVelocity().getY();
        double speed = Math.sqrt(vx * vx + vy * vy);
        double omegaRoll = ball.getOmegaRoll();
        double omegaZ    = ball.getOmegaZ();
        double R         = ball.getRadius();

        if (speed > 0.01) {
            double heading = Math.atan2(vy, vx);
            double slip = speed - R * omegaRoll;

            if (slip > 0.01) {
                // Sliding: kinetic friction decelerates v, accelerates roll
                double decel     = MU_K * G_EFF;
                double alphaRoll = 5.0 * MU_K * G_EFF / (2.0 * R);
                vx -= decel * Math.cos(heading);
                vy -= decel * Math.sin(heading);
                double newSpeed = Math.sqrt(vx * vx + vy * vy);
                omegaRoll = Math.min(omegaRoll + alphaRoll, newSpeed / R);
            } else {
                // Pure rolling: rolling friction slows everything together
                double decel = 5.0 / 7.0 * MU_R * G_EFF;
                vx -= decel * Math.cos(heading);
                vy -= decel * Math.sin(heading);
                double newSpeed = Math.sqrt(vx * vx + vy * vy);
                if (newSpeed < 0.05) { vx = 0; vy = 0; omegaRoll = 0; }
                else                   omegaRoll = newSpeed / R;
            }
        } else {
            vx = 0; vy = 0;
        }

        // Side-spin decays independently (pivoting friction against cloth)
        if (Math.abs(omegaZ) > 0.001) {
            double alphaZ     = -Math.signum(omegaZ) * 5.0 * MU_S * G_EFF / (2.0 * R);
            double nextOmegaZ = omegaZ + alphaZ;
            omegaZ = (Math.signum(nextOmegaZ) != Math.signum(omegaZ)) ? 0.0 : nextOmegaZ;
        }

        ball.setVelocity(new Point2D.Double(vx, vy));
        ball.setOmegaRoll(omegaRoll);
        ball.setOmegaZ(omegaZ);
    }

    private void capSpeeds(LinkedList<Ball> balls) {
        for (Ball ball : balls) {
            double vx = ball.getVelocity().getX();
            double vy = ball.getVelocity().getY();
            double speed = Math.sqrt(vx * vx + vy * vy);
            if (speed > MAX_SPEED) {
                double scale = MAX_SPEED / speed;
                ball.setVelocity(new Point2D.Double(vx * scale, vy * scale));
            }
        }
    }

    private boolean wouldExceedBounds(Ball ball, double dx, double dy) {
        double newX = ball.getLocation().getX() + dx;
        double newY = ball.getLocation().getY() + dy;
        return newX < ball.getRadius() || newX > dims.getX() - ball.getRadius()
            || newY < ball.getRadius() || newY > dims.getY() - ball.getRadius();
    }
}
