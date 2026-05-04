package edu.rice.comp504.model.strategy.interactStrategy;
import edu.rice.comp504.model.paint.Ball;


/**
 * The unwrapper has classes that differentiate the strategies that may be wrapped at different depths within one
 * particular interact strategy.
 */
public class InteractStrategyUnwrapper {

    private static InteractStrategyUnwrapper my_int_unwrapper;

    /**
     * the constructor
      */
    private InteractStrategyUnwrapper() { }

    /**
     * The unwrapper is a singleton.
     * @return the singleton instance of the unwrapper
     */
    public static InteractStrategyUnwrapper makeUnwrapper() {
        if(my_int_unwrapper == null) {
            my_int_unwrapper = new InteractStrategyUnwrapper();
        }
            return my_int_unwrapper;
    }

    /**
     * retrieve a particular base strategy from a strategy that carries wrappers.
      * @param context
     * @param strategy_name
     * @return unwrapped base strategy
     */
    public IInteractStrategy getBaseInteractStrategy(Ball context, String strategy_name) {

        IInteractStrategy here_it_is = null;

        if(context.getInteractStrategy().getName().contains(strategy_name)) {
            here_it_is = context.getInteractStrategy();
            if(here_it_is.getName().contains("override_interact")) {
                OverrideInteractStrategy oistr = (OverrideInteractStrategy) here_it_is;
                here_it_is = oistr.getWrappedStrategy();
            }
            if(here_it_is.getName().contains("persistencelimit_interact")) {
                PersistenceLimitWrapperInteractStrategy pswstr = (PersistenceLimitWrapperInteractStrategy) here_it_is;
                here_it_is = pswstr.getWrappedStrategy();
            }
            if(here_it_is.getName().contains("switcher")) {
                SwitcherInteractStrategy swintstr = (SwitcherInteractStrategy) here_it_is;
                here_it_is = swintstr.getWrappedStrategy();
            }
            if(here_it_is.getName().contains("composite")) {
                CompositeInteractStrategy compstr = (CompositeInteractStrategy) here_it_is;
                here_it_is = compstr.getBaseStrategy(strategy_name);
            }
        }

        return here_it_is;
    }


    //Wrapper Levels:
    //Override (Top)
    //PersistenceLimit
    //Switcher
    //Composite (not a wrapper)
    //Base Strategies

    //we look at what we want to remove, and what comes right before that in the string.
    //we have five levels to think about.
    //we start at the top. for each, we say, is the wrapped strategy of the type we're looking for? If not, we keep
    //digging. If so, we hoist its wrapped strategy.
    //we should look at the wording of the strategy.

    /**
     *
     * Surgically remove a particular wrapper
     *
     * @param context
     * @param wrapper
     */

    public void removeWrapper(Ball context, String wrapper) {

        IInteractWrapperStrategy here_it_is = (IInteractWrapperStrategy) context.getInteractStrategy();

        if (here_it_is.getName().contains(wrapper)) {
            //is the wrapper to remove at the top?
            if (here_it_is.getName().startsWith(wrapper)) {
                context.setInteractStrategy(here_it_is.getWrappedStrategy());
            }
            //is the wrapper to remove under the top?
            else if (here_it_is.getWrappedStrategy() instanceof IInteractWrapperStrategy) {
                IInteractWrapperStrategy my_wrap = (IInteractWrapperStrategy) here_it_is.getWrappedStrategy();
                if (my_wrap.getName().startsWith(wrapper)) {
                    here_it_is.setWrappedStrategy(my_wrap.getWrappedStrategy());
                    context.setInteractStrategy(here_it_is);
                }
                //is the wrapper to remove under that?
                else if (my_wrap.getWrappedStrategy() instanceof IInteractWrapperStrategy) {
                    IInteractWrapperStrategy my_bottom_wrap = (IInteractWrapperStrategy) my_wrap.getWrappedStrategy();
                    if (my_bottom_wrap.getName().startsWith(wrapper)) {
                        my_wrap.setWrappedStrategy(my_bottom_wrap.getWrappedStrategy());
                        here_it_is.setWrappedStrategy(my_wrap);
                        context.setInteractStrategy(here_it_is);
                    }
                }
            }
        }
    }

}
