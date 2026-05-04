package edu.rice.comp504.model;

import edu.rice.comp504.model.cmd.DeletionCommand;
import edu.rice.comp504.model.cmd.SwitchInteractStrategyCommand;
import edu.rice.comp504.model.cmd.SwitchUpdateStrategyCommand;
import edu.rice.comp504.model.cmd.UpdateCommand;
import edu.rice.comp504.model.paint.*;
import edu.rice.comp504.model.strategy.interactStrategy.*;
import edu.rice.comp504.model.strategy.updateStrategy.*;

import java.awt.*;
import java.util.LinkedList;




/**
 * Note: Balls are only accessed through using the observable-observer design pattern.  You should NOT create
 * a separate list of balls.
 */
public class DispatchAdapter extends BallObservable {

    private Point dims;
    private UpdateStrategyFactory my_upfact;
    private InteractStrategyFactory my_intfact;
    public int break_it;

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

        //delete and add observers as necessary


        setChanged();
        notifyObservers(new UpdateCommand(dims, this, countObservers()));

        setChanged();
        notifyObservers(new DeletionCommand(dims, this));

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
        Ball my_ball = makeBall(up_str, int_str);
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
     * helper for the ball constructor; makes random values
     * @param uStrategy
     * @param iStrategy
     * @return A new ball
     */
    public Ball makeBall(IUpdateStrategy uStrategy, IInteractStrategy iStrategy) {
        return new Ball(new Point((int) Math.floor(Math.random() * this.dims.x),
                (int) Math.floor(Math.random() * this.dims.y)), (int) Math.floor(Math.random() * 40 + 10),
                new Point((int) Math.floor(Math.random() * 25 + 1), (int) Math.floor(Math.random() * 25) + 1),
                "rgb(" + (int)Math.floor(Math.random()*255)+","+ (int)Math.floor(Math.random()*255)+ ","
                        +(int)Math.floor(Math.random()*255)+")", uStrategy, iStrategy);
    }


}
