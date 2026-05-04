package edu.rice.comp504.model.cmd;

import edu.rice.comp504.model.paint.Ball;
import edu.rice.comp504.model.strategy.updateStrategy.IUpdateStrategy;

/**
 * This command delivers the chosen update strategy to the switcher ball when switch is pressed.
 */
public class SwitchUpdateStrategyCommand implements IBallCmd {

    private IUpdateStrategy u_str;

    /**
     * the constructor
     * @param u_str
     */
    public SwitchUpdateStrategyCommand(IUpdateStrategy u_str) {
        this.u_str = u_str;
    }

    /**
     * Determines if switch should occur, then sets new strategy.
     * @param context The receiver on which the command is executed.
     */
  public void execute(Ball context) {
        if (context.getUpdateStrategy().getName().contains("switcher")) {
            context.setUpdateStrategy(this.u_str);
        }
    }
}
