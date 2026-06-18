package edu.rice.comp504.model;

import edu.rice.comp504.model.paint.Ball;

public class Pocket {
    public double x, y;
    public int radius;
    public String flashColor;
    private transient int flashFrames = 0;

    public Pocket(double x, double y, int radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public boolean canSwallow(Ball ball) {
        double dx = ball.getLocation().getX() - x;
        double dy = ball.getLocation().getY() - y;
        return Math.sqrt(dx * dx + dy * dy) <= radius && ball.getRadius() <= radius;
    }

    public void startFlash(String ballColor) {
        flashColor = averageColor(ballColor);
        flashFrames = 1;
    }

    public void tickFlash() {
        if (flashFrames > 0 && --flashFrames == 0) flashColor = null;
    }

    private String averageColor(String ballColor) {
        // table-dark non-black stripe: #a8a8a8 = rgb(168,168,168)
        int tr = 168, tg = 168, tb = 168;
        String inner = ballColor.replaceAll("[^0-9,]", "");
        String[] parts = inner.split(",");
        int br = Integer.parseInt(parts[0].trim());
        int bg = Integer.parseInt(parts[1].trim());
        int bb = Integer.parseInt(parts[2].trim());
        return "rgb(" + (tr + br) / 2 + "," + (tg + bg) / 2 + "," + (tb + bb) / 2 + ")";
    }
}
