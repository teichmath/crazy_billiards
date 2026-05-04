package edu.rice.comp504.model.strategy.updateStrategy;

import edu.rice.comp504.model.paint.Ball;

/**
 * The unwrapper has classes that differentiate the strategies that may be wrapped at different depths within one
 * particular interact strategy.
 */
public class UpdateStrategyUnwrapper {

    private static UpdateStrategyUnwrapper my_up_unwrapper;

    /**
     * the constructor
     */
    private UpdateStrategyUnwrapper() { }

    /**
     * the unwrapper is a singleton
     * @return singleton instance of unwrapper
     */
    public static UpdateStrategyUnwrapper makeUnwrapper() {
        if(my_up_unwrapper == null) {
            my_up_unwrapper = new UpdateStrategyUnwrapper();
        }
        return my_up_unwrapper;
    }

    //Wrapper Levels:
    //Override (Top)
    //Eaten
    //Follower
    //Switcher
    //Composite (not a wrapper)
    //Base Strategies

    /**
     * take away a specified wrapper.
     * @param context
     * @param wrapper
     */
    public void removeWrapper(Ball context, String wrapper) {

        IUpdateWrapperStrategy here_it_is = (IUpdateWrapperStrategy) context.getUpdateStrategy();

        if (here_it_is.getName().contains(wrapper)) {
            //is the wrapper to remove at the top?
            if (here_it_is.getName().startsWith(wrapper)) {
                context.setUpdateStrategy(here_it_is.getWrappedStrategy());
            }
            //is the wrapper to remove under the top?
            else if (here_it_is.getWrappedStrategy() instanceof IUpdateWrapperStrategy) {
                IUpdateWrapperStrategy my_wrap = (IUpdateWrapperStrategy) here_it_is.getWrappedStrategy();
                if (my_wrap.getName().startsWith(wrapper)) {
                    here_it_is.setWrappedStrategy(my_wrap.getWrappedStrategy());
                    context.setUpdateStrategy(here_it_is);
                }
                //is the wrapper to remove under that?
                else if (my_wrap.getWrappedStrategy() instanceof IUpdateWrapperStrategy) {
                    IUpdateWrapperStrategy my_lower_wrap = (IUpdateWrapperStrategy) my_wrap.getWrappedStrategy();
                    if (my_lower_wrap.getName().startsWith(wrapper)) {
                        my_wrap.setWrappedStrategy(my_lower_wrap.getWrappedStrategy());
                        here_it_is.setWrappedStrategy(my_wrap);
                        context.setUpdateStrategy(here_it_is);
                    }
                    //last level: is the wrapper here, finally?
                    else if (my_lower_wrap.getWrappedStrategy() instanceof IUpdateWrapperStrategy) {
                        IUpdateWrapperStrategy my_bottom_wrap = (IUpdateWrapperStrategy) my_lower_wrap.getWrappedStrategy();
                        if(my_bottom_wrap.getName().startsWith(wrapper)) {
                            my_lower_wrap.setWrappedStrategy(my_bottom_wrap.getWrappedStrategy());
                            my_wrap.setWrappedStrategy(my_lower_wrap.getWrappedStrategy());
                            here_it_is.setWrappedStrategy(my_wrap);
                            context.setUpdateStrategy(here_it_is);
                        }
                    }
                }
            }
        }
    }

}
