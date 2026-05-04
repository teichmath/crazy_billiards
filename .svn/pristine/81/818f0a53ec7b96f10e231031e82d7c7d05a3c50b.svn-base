import edu.rice.comp504.model.DispatchAdapter;
import edu.rice.comp504.model.paint.Ball;
import edu.rice.comp504.model.strategy.interactStrategy.BilliardStrategy;
import edu.rice.comp504.model.strategy.updateStrategy.StraightStrategy;
import junit.framework.TestCase;

import java.awt.*;

public class DispatchAdapterTest extends TestCase {

    @org.junit.Test
    public void testLoadBall() {
        DispatchAdapter dad = new DispatchAdapter();

        Ball testnullball = dad.loadBall("switcher&false&updatestrategies& &interactstrategies");

        assertTrue("no strategies makes null ball color test", testnullball.getColor().equals("black"));
        assertTrue("no strategies makes null ball vel test", testnullball.getVelocity().getX() == 0
                && testnullball.getVelocity().getY() == 0);
        assertTrue("no strategies makes null ball type test",
                testnullball.getUpdateStrategy().getName().equals("null_update"));

        Ball collider_a = new Ball(new Point(50, 50), 10, new Point(10, 10), "red",
                StraightStrategy.makeStrategy(), new BilliardStrategy());

        Ball collider_b = new Ball(new Point(60, 60), 10, new Point(-10, -10), "red",
                StraightStrategy.makeStrategy(), new BilliardStrategy());

        collider_a.ballCollision(collider_b);

        assertTrue("balls change velocities correctly on collision",
                collider_a.getVelocity().getX() == -10 && collider_a.getVelocity().getY() == -10
                        && collider_b.getVelocity().getX() == 10 && collider_b.getVelocity().getY() == 10);

    }

}
