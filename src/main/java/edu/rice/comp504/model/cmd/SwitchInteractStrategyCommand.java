package edu.rice.comp504.model.cmd;

import edu.rice.comp504.model.paint.Ball;
import edu.rice.comp504.model.strategy.interactStrategy.IInteractStrategy;

/**
 * This command delivers the chosen interact strategy to the switcher ball when switch is pressed.
 */
public class SwitchInteractStrategyCommand implements IBallCmd {

    private IInteractStrategy int_str;

    /**
     * the constructor
     * @param int_str
     */
    public SwitchInteractStrategyCommand(IInteractStrategy int_str) {
        this.int_str = int_str;
    }

    /**
     * Execute the command by the receiver (context): set the interact strategy.
     * @param context  The receiver that will execute the command.
     */
    public void execute(Ball context){
        if (context.getInteractStrategy().getName().contains("switcher")) {
            context.setInteractStrategy(this.int_str);
        }
    }
}
