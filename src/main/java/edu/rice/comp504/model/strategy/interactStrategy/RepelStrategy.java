package edu.rice.comp504.model.strategy.interactStrategy;

import edu.rice.comp504.model.paint.Ball;

import java.awt.*;

/**
 * A ball with this strategy exerts a repulsive force on any other ball within its influence radius.
 * Force magnitude = K / d^3, directed away from this ball.
 * Influence radius: 2r for r < M, else r + M (M = 40 px).
 */
public class RepelStrategy implements IInteractStrategy {

    private static double K = 3000000.0;
    private static int M = 1000;
    private static double Q = 14.0;

    private static IInteractStrategy my_iinteract;

    private RepelStrategy() {}

    public static IInteractStrategy makeStrategy() {
        if (my_iinteract == null) my_iinteract = new RepelStrategy();
        return my_iinteract;
    }

    public static void setK(double k) { K = k; }
    public static void setM(int m) { M = m; }
    public static void setQ(double q) { Q = q; }

    public static double influenceRadius(int radius) {
        return radius < M / (Q - 1) ? Q * radius : radius + M;
    }

    public String getName() { return "repel"; }

    public void interact(Ball src, Ball dest) {
        double dx = dest.getLocation().getX() - src.getLocation().getX();
        double dy = dest.getLocation().getY() - src.getLocation().getY();
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist < 1.0) return;

        double forceMag = K / (dist * dist * dist);
        double fx = forceMag * dx / dist;
        double fy = forceMag * dy / dist;

        dest.setVelocity(new Point(
                (int) Math.round(dest.getVelocity().getX() + fx),
                (int) Math.round(dest.getVelocity().getY() + fy)
        ));
    }
}
