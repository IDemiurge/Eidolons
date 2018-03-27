package tests.crawl;

import main.entity.obj.DC_Obj;
import main.entity.obj.Structure;
import main.game.battlecraft.logic.battlefield.vision.VisionManager;

import static org.junit.Assert.assertTrue;

/**
 * Created by JustMe on 10/30/2017.
 * <p>
 * move unit around only on inside/outside and keep checking that the cells remain undetected
 * on outside/inside. Then at the end, check that all walls are Known...
 * Advanced: engagement test!
 */
public class JUnitDetectionTest extends JUnitClearshotTest {
    @Override
    protected void checkObj(DC_Obj sub, boolean hero_inside, boolean inside) {
        if (VisionManager.checkDetected(sub)) {
            assertTrue(false);
        }
//    game.getDungeonMaster().getExplorationMaster().getCrawler().checkStatusUpdate();
//        List<Unit> group = AggroMaster.getAggroGroup();
    }

    @Override
    public void makeChecks() {
        super.makeChecks();
        checkWallsDetected();
    }

    private void checkWallsDetected() {
        for (Structure sub : game.getStructures()) {
            if (!VisionManager.checkKnownForPlayer(sub)) {
                assertTrue(false);
            }
        }
    }
}
