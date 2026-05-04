package edu.rice.comp504.model.strategy.updateStrategy;

import edu.rice.comp504.model.paint.Ball;

import java.util.LinkedList;

/**
 * The CompositeStrategy uses the Composite Design Pattern to update the ball.   As a result, it calls
 * the updateState method on each child
 */
public class CompositeUpdateStrategy implements IUpdateStrategy {
    private LinkedList<IUpdateStrategy> children;

    /**
     * Constructor
     */
    public CompositeUpdateStrategy(LinkedList<IUpdateStrategy> children) {
        this.children = children;
    }


    /**
     * Get a string of all strategy names in the composite
     * @return String that includes all the names
     */
    public String getName() {
        String name_string =  "composite_update ";
        for(IUpdateStrategy element : children) name_string += element.getName() + " ";
        return name_string;
    }

    /**
     * Update the ball state in the ball world
     * @param context The ball to update
     */
    public void updateState(Ball context) {
        for(IUpdateStrategy element: children) {
            element.updateState(context);
        }
    }
}
