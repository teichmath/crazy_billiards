package edu.rice.comp504.model.cmd;

import edu.rice.comp504.model.DispatchAdapter;
import edu.rice.comp504.model.paint.Ball;
import edu.rice.comp504.model.strategy.interactStrategy.*;
import edu.rice.comp504.model.strategy.updateStrategy.FollowerWrapperUpdateStrategy;
import edu.rice.comp504.model.strategy.interactStrategy.RepelStrategy;

import java.awt.*;
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


    /**
     * The constructor
     *
     * @param dims The dispatch adapter
     */
    public UpdateCommand(Point dims, DispatchAdapter dad, int group_size) {
        this.dims = dims;
        this.dad = dad;
        this.update_complete = false;
        this.group_size = group_size;
        this.my_int_unwrapper = InteractStrategyUnwrapper.makeUnwrapper();
    }

    /**
     * Master method for collision and updating logic. Establishes new placement for context ball.
     *
     * @param context The paintobj.
     */
    public void execute(Ball context) {
        System.out.println("execute hello 1");
        boolean interacted = false;
        context.wallCollision(dims);
        if (!context.getInteractStrategy().getName().contains("override_interact")) {
otherloop:  for (Ball other : ball_group) {
                if (!other.getInteractStrategy().getName().contains("override_interact")) {
                    System.out.println("execute hello 2");

                    for(int i = 0; i < 2; i++) {
                        Ball a = context;
                        Ball b = other;
                        System.out.println("execute hello 3");
                        if(i == 1) {
                            a = other;
                            b = context;
                        }
                        if(a.getUpdateStrategy().getName().startsWith("follower")) {
//                            if(b.getUpdateStrategy().getName().startsWith("follower")) continue otherloop;
                            FollowerWrapperUpdateStrategy fwstrat = (FollowerWrapperUpdateStrategy) a.getUpdateStrategy();
                            if(fwstrat.getLeader() == b) continue otherloop;
                        }
                    }
                    System.out.println("execute hello 4");

                    Point record_context_vel = context.getVelocity();
                    Point record_other_vel = other.getVelocity();

                    if (context.ballCollision(other, dad)) {
                        interacted = true;
                        other.ballCollision(context, dad);
                        System.out.println("execute hello 5");

                        if (context.getInteractStrategy().getName().contains("billiard"))
                            context.setVelocity(record_context_vel);
                        if (other.getInteractStrategy().getName().contains("billiard"))
                            other.setVelocity(record_other_vel);
                        System.out.println("execute hello 6");

                        Ball a = context;
                        Ball b = other;
                        if(other.getInteractStrategy().getName().contains("swapper") && !context.getInteractStrategy().getName().contains("swapper")) {
                            a = (b == context) ? context : other;
                            b = (a == context) ? other : context;
                        }
                        if(other.getInteractStrategy().getName().contains("blob") && !context.getInteractStrategy().getName().contains("blob")) {
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

//the update strategies perform the actual placements of the balls (among other effects);
// no balls have been moved until this moment.

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
                        ball.setVelocity(new Point((int) (ball.getVelocity().getX() + bstr.getForce().getX()),
                                (int) (ball.getVelocity().getY() + bstr.getForce().getY())));
                        bstr.zeroForce();
                    }
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

            // Repel influence: apply force to balls within influence radius but not yet touching
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
                if(!updated[i]) {
                    Ball ball = ball_group.get(i);
                    System.out.println("execute hello 16");
                    if (ball.getInteractStrategy().getName().contains("billiard")) {
                        BilliardStrategy bstr = (BilliardStrategy)
                                my_int_unwrapper.getBaseInteractStrategy(ball, "billiard");
                        System.out.println("execute hello 17");
                        ball.setVelocity(new Point((int) (ball.getVelocity().getX() + bstr.getForce().getX()),
                                (int) (ball.getVelocity().getY() + bstr.getForce().getY())));
                        bstr.zeroForce();
                    }
                    ball.getUpdateStrategy().updateState(ball);
                    System.out.println("execute hello 18");
                }
            }

            //adjust overlapping balls
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

                    if (distance < 0.5) { dx = 1; dy = 0; distance = 1; } // avoid div-by-zero
                    double nx = dx / distance; // unit vector from bj toward bi
                    double ny = dy / distance;
                    double half = overlap / 2.0;

                    // Determine how much each ball can move (wall-constrained balls give their share to the other)
                    boolean bi_blocked = wouldExceedBounds(bi, nx * half, ny * half);
                    boolean bj_blocked = wouldExceedBounds(bj, -nx * half, -ny * half);
                    double bi_push = bi_blocked ? 0 : (bj_blocked ? overlap : half);
                    double bj_push = bj_blocked ? 0 : (bi_blocked ? overlap : half);

                    // Position correction
                    if (bi_push > 0) bi.nextLocation((int) Math.round(nx * bi_push), (int) Math.round(ny * bi_push));
                    if (bj_push > 0) bj.nextLocation((int) Math.round(-nx * bj_push), (int) Math.round(-ny * bj_push));

                    // Velocity correction: zero out inward components so balls don't re-penetrate
                    if (bi_push > 0) {
                        double vi_n = bi.getVelocity().getX() * nx + bi.getVelocity().getY() * ny;
                        if (vi_n < 0) bi.setVelocity(new Point(
                                (int) Math.round(bi.getVelocity().getX() - nx * vi_n),
                                (int) Math.round(bi.getVelocity().getY() - ny * vi_n)));
                    }
                    if (bj_push > 0) {
                        double vj_n = bj.getVelocity().getX() * nx + bj.getVelocity().getY() * ny;
                        if (vj_n > 0) bj.setVelocity(new Point(
                                (int) Math.round(bj.getVelocity().getX() - nx * vj_n),
                                (int) Math.round(bj.getVelocity().getY() - ny * vj_n)));
                    }
                }
            }

            capSpeeds(ball_group);

            //final wall adjustments
            for (int i = 0; i < ball_group.size(); i++) {
                System.out.println("execute hello 20");
                Ball bi = ball_group.get(i);
                boolean outcome = bi.wallCollision(dims);
                if(outcome) System.out.println("ball "+i+" hits! loc: "+bi.getLocation().getX()+", "+bi.getLocation().getY());
                System.out.println("execute hello 21");
            }


            update_complete = true;
        }
    }

    private void capSpeeds(LinkedList<Ball> balls) {
        for (Ball ball : balls) {
            double vx = ball.getVelocity().getX();
            double vy = ball.getVelocity().getY();
            double speed = Math.sqrt(vx * vx + vy * vy);
            if (speed > MAX_SPEED) {
                double scale = MAX_SPEED / speed;
                ball.setVelocity(new Point(
                        (int) Math.round(vx * scale),
                        (int) Math.round(vy * scale)));
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


