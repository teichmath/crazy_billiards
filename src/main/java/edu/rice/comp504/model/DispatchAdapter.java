package edu.rice.comp504.model;

import edu.rice.comp504.model.cmd.DeletionCommand;
import edu.rice.comp504.model.cmd.ImpulseCommand;
import edu.rice.comp504.model.cmd.SwitchInteractStrategyCommand;
import edu.rice.comp504.model.cmd.SwitchUpdateStrategyCommand;
import edu.rice.comp504.model.cmd.UpdateCommand;
import edu.rice.comp504.model.paint.*;
import edu.rice.comp504.model.strategy.interactStrategy.*;
import edu.rice.comp504.model.strategy.updateStrategy.*;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;




/**
 * Note: Balls are only accessed through using the observable-observer design pattern.  You should NOT create
 * a separate list of balls.
 */
public class DispatchAdapter extends BallObservable {

    private Point dims;
    private UpdateStrategyFactory my_upfact;
    private InteractStrategyFactory my_intfact;
    public int break_it;
    public int frameCount;
    private LinkedList<Pocket> pockets = new LinkedList<>();

    /**
     * Constructor
     */
    public DispatchAdapter() {

        this.dims = new Point(800,800);
        my_upfact = UpdateStrategyFactory.makeFactory();
        my_intfact = InteractStrategyFactory.makeFactory();
        break_it = 0;
    }

     /**
     * Set the canvas dimensions
     * @param dims The canvas width (x) and height (y)
     */
    public void setCanvasDims(Point dims) {
        this.dims = dims;
        my_upfact.setDims(dims);
    }

    /**
     * Get the canvas dimensions
     * @return The canvas dimensions
     */
    public Point getCanvasDims() {
        return dims;
    }


    /**
     * Call the update method on all the paint observers to update their position in the paint world
     */
    public void updateBallWorld() {

        // Tick pocket flash counters first so flash lasts exactly one outgoing frame
        synchronized (this) {
            for (Pocket p : pockets) p.tickFlash();
        }

        setChanged();
        notifyObservers(new UpdateCommand(dims, this, countObservers()));

        setChanged();
        notifyObservers(new DeletionCommand(dims, this));

        // Check whether any ball has fallen into a pocket
        LinkedList<Pocket> snap;
        synchronized (this) { snap = new LinkedList<>(pockets); }

        List<BallObserver> toRemove = new LinkedList<>();
        for (BallObserver o : getObservers()) {
            Ball ball = (Ball) o;
            for (Pocket pocket : snap) {
                if (pocket.canSwallow(ball)) {
                    pocket.startFlash(ball.getColor());
                    toRemove.add(o);
                    break;
                }
            }
        }
        for (BallObserver o : toRemove) deleteObserver(o);
    }

    /**
     * Add a pocket at canvas position (x, y) with the given radius.
     * Validates that the center is within bounds and does not overlap any existing pocket.
     * @return true if the pocket was added
     */
    public synchronized boolean addPocket(double x, double y, int radius) {
        if (x < 0 || x > dims.x || y < 0 || y > dims.y) return false;
        for (Pocket p : pockets) {
            double dx = p.x - x, dy = p.y - y;
            if (Math.sqrt(dx * dx + dy * dy) < p.radius + radius) return false;
        }
        pockets.add(new Pocket(x, y, radius));
        return true;
    }

    /** Remove all pockets. */
    public synchronized void clearPockets() {
        pockets.clear();
    }

    /**
     * Load a paint into the paint world
     * @param body  The REST request body has the strategy names.
     * @return A new ball
     */
    public Ball loadBall(String body) {

        System.out.println(body);

        String[] body_words = extractBodyWords(body);

        for(String word: body_words) { System.out.println(word); }

        boolean switcher = retrieveSwitch(body_words);
        IUpdateStrategy up_str = retrieveUpdateStrategy(body_words, switcher);
        IInteractStrategy int_str = retrieveInteractStrategy(body_words, switcher);
        String colorOverride = retrieveColor(body_words);
        Ball my_ball = makeBall(up_str, int_str, colorOverride);
        addObserver(my_ball);
        return my_ball;

    }

    /**
     * Switch the strategy for all the switcher balls
     * @param body  The REST request body containing the new strategy.
     */
    public void switchStrategy(String body) {
        System.out.println("SWITCH! "+body);
        String[] body_words = extractBodyWords(body);
        for(String word: body_words) { System.out.println(word); }
        int switch_type = retrieveSwitchType(body_words);
        System.out.println("switch type: "+switch_type);
        if(switch_type == 0 || switch_type == 1) {
            IUpdateStrategy up_str = retrieveUpdateStrategy(body_words, true);
            setChanged();
            notifyObservers(new SwitchUpdateStrategyCommand(up_str));
            clearChanged();
        }
        if(switch_type == 0 || switch_type == 2) {
            IInteractStrategy int_str = retrieveInteractStrategy(body_words, true);
            setChanged();
            notifyObservers(new SwitchInteractStrategyCommand(int_str));
            clearChanged();
        }
    }

    /**
     * Get knowledge of whether or not we want to make a switcher ball
     * @param words
     * @return true or false for switcher
     */
    public boolean retrieveSwitch(String[] words) {
        boolean switcher = false;
        for(int i = 0; i < words.length; i++) {
            if(words[i].equals("switcher")) {
                if(words[i+1].equals("true")) switcher = true;
                break;
            }
        }
        return switcher;
    }

    /**
     * Find out whether our switch will affect update strategy, interaction, or both
     * @param words
     * @return a code that tells us what kind of switch to do
     */
    public int retrieveSwitchType(String[] words) {
        int switch_type = 0;
        for(int i = 0; i < words.length; i++) {
            if(words[i].equals("what_to_switch")) {
                if(words[i+1].equals("1")) switch_type = 1;
                if(words[i+1].equals("2")) switch_type = 2;
                break;
            }
        }
        return switch_type;
    }

    /**
     * get the update strategy specified by the body
      * @param words
     * @param switcher
     * @return the update strategy we want
     */
    public IUpdateStrategy retrieveUpdateStrategy(String[] words, Boolean switcher) {

        System.out.println("in update strat "+words.length);
        boolean start_record = false;
        LinkedList<IUpdateStrategy> updaters = new LinkedList<>();

        //loop through words
        for (String word : words) {
            if (word.equals("updatestrategies")) {
                start_record = true;
                continue;
            }
            if (!start_record) continue;
            if (word.equals(" ") || word.equals("") || word.equals("interactstrategies")) {
                if(updaters.size() == 0) updaters.add(my_upfact.makeStrategy("null"));
                break;
            }

            IUpdateStrategy new_strategy = my_upfact.makeStrategy(word);
            updaters.add(new_strategy);
        }

        if (updaters.size() == 1) {
            if (switcher) {
                return new SwitcherUpdateStrategy(updaters.get(0));
            } else return updaters.get(0);
        } else {
            if (switcher) return new SwitcherUpdateStrategy(new CompositeUpdateStrategy(updaters));
            else return new CompositeUpdateStrategy(updaters);
        }
    }


    /**
     * get the interact strategy specified by the body
      * @param words
     * @param switcher
     * @return the interact strategy we want
     */
    public IInteractStrategy retrieveInteractStrategy(String[] words, Boolean switcher) {

        Boolean start_record = false;
        LinkedList<IInteractStrategy> interacters = new LinkedList<>();

        //loop through words
        for (String word : words) {
            if (word.equals("interactstrategies")) {
                start_record = true;
                continue;
            }
            if (!start_record) continue;
            if (word.equals(" ") || (word.equals("updatestrategies"))) {
                if(interacters.size() == 0) interacters.add(my_intfact.makeStrategy("null"));
                break;
            }

            IInteractStrategy new_strategy = my_intfact.makeStrategy(word);
            interacters.add(new_strategy);
        }

        if (interacters.size() == 1) {
            if (switcher) {
                return new SwitcherInteractStrategy(interacters.get(0));
            } else return interacters.get(0);
        } else {
            if (switcher) return new SwitcherInteractStrategy(new CompositeInteractStrategy(interacters));
            else return new CompositeInteractStrategy(interacters);
        }
    }

    /**
     * Transfer words in body parameter to a string array
     *
     * @param body
     * @return a string array
     */
    private String[] extractBodyWords(String body) {
        String[] words = body.split("=|\\+|\\&");
        return words;
    }

    /**
     * Apply an impulse at canvas position (x, y) in direction angle with the given strength.
     * Only the ball whose area contains (x, y) is affected.
     */
    public void applyImpulse(double x, double y, double angle, double strength) {
        ImpulseCommand cmd = new ImpulseCommand(x, y, angle, strength);
        System.out.println("impulse x=" + x + " y=" + y + " angle=" + angle + " strength=" + strength);
        for (BallObserver o : getObservers()) {
            try {
                cmd.execute((Ball) o);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Resolve a color name keyword to an rgb string, or return null for random.
     */
    public String retrieveColor(String[] words) {
        for (int i = 0; i < words.length - 1; i++) {
            if (words[i].equals("color")) {
                switch (words[i + 1].toLowerCase()) {
                    case "white":  return "rgb(255,255,255)";
                    case "red":    return "rgb(210,50,50)";
                    case "orange": return "rgb(230,130,30)";
                    case "yellow": return "rgb(240,210,30)";
                    case "green":  return "rgb(50,180,50)";
                    case "blue":   return "rgb(50,100,210)";
                    case "purple": return "rgb(150,50,210)";
                    case "black":  return "rgb(20,20,20)";
                    default:       return null;
                }
            }
        }
        return null;
    }

    /** Convenience overload — random color. */
    public Ball makeBall(IUpdateStrategy uStrategy, IInteractStrategy iStrategy) {
        return makeBall(uStrategy, iStrategy, null);
    }

    /**
     * Build a new ball with random position/velocity. colorOverride is an rgb string;
     * pass null for a fully random color.
     */
    public Ball makeBall(IUpdateStrategy uStrategy, IInteractStrategy iStrategy, String colorOverride) {
        String color = (colorOverride != null) ? colorOverride :
                "rgb(" + (int)Math.floor(Math.random()*255) + ","
                       + (int)Math.floor(Math.random()*255) + ","
                       + (int)Math.floor(Math.random()*255) + ")";
        Ball ball = new Ball(
                new Point2D.Double(Math.random() * this.dims.x, Math.random() * this.dims.y),
                (int) Math.floor(Math.random() * 40 + 10),
                new Point2D.Double(Math.floor(Math.random() * 25 + 1), Math.floor(Math.random() * 25) + 1),
                color, uStrategy, iStrategy);
        String upName = uStrategy.getName();
        if (!upName.contains("wander") && !upName.contains("mowthelawn")
                && !upName.contains("cornergravity") && !upName.contains("drag")) {
            ball.setFrictionFactor(0.995);
        }
        return ball;
    }


}
