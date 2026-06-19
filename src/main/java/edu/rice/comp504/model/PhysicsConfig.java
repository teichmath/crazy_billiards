package edu.rice.comp504.model;

/**
 * Global singleton holding all tunable physics constants.
 * Fields are volatile so slider updates on web-server threads are visible
 * to the game-update thread immediately.
 */
public class PhysicsConfig {

    private static final PhysicsConfig INSTANCE = new PhysicsConfig();
    public static PhysicsConfig get() { return INSTANCE; }
    private PhysicsConfig() {}

    // --- Cloth friction (UpdateCommand) ---
    public volatile double gEff    = 1.0;   // deceleration scale (px/frame²)
    public volatile double muK     = 0.3;   // kinetic (sliding) friction
    public volatile double muR     = 0.1;   // rolling friction
    public volatile double muS     = 0.05;  // side-spin decay

    // --- Cushion / rail (Ball.wallCollision) ---
    public volatile double eps     = 0.7;   // rail restitution
    public volatile double muC     = 0.1;   // rail friction
    public volatile double gamma   = 0.15;  // spin → velocity transfer at rail
    public volatile double beta    = 0.5;   // spin drain at rail

    // --- Ball-on-ball (Ball.ballCollision + BilliardStrategy) ---
    public volatile double eBall   = 0.95;  // ball-ball restitution
    public volatile double muB     = 0.04;  // throw friction
    public volatile double alphaB  = 0.5;   // spin throw efficiency

    // --- Cue (ImpulseCommand) ---
    public volatile double cueScale = 5.0;  // speed multiplier for cue strength
}
