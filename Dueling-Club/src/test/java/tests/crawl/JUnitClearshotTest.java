package tests.crawl;

import main.content.enums.rules.VisionEnums.OUTLINE_TYPE;
import main.content.enums.rules.VisionEnums.VISIBILITY_LEVEL;
import main.entity.obj.DC_Obj;
import main.game.bf.Coordinates;
import tests.SpecialLevelTest;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by JustMe on 10/26/2017.
 */
public class JUnitClearshotTest extends SpecialLevelTest {

    @Override
    public String getLevelPath() {
        return "test\\clearshot test new.xml";
    }

    public boolean isDiagonal() {
        return false;
    }

    public int getInnerWidth() {
        return 6;
    }

    public int getInnerHeight() {
        return 6;
    }

    public void check(boolean hero_inside, boolean inside, boolean cellsOrObjects) {
        List<? extends DC_Obj> list = getObjects(inside, cellsOrObjects);
        OUTLINE_TYPE outline = OUTLINE_TYPE.BLOCKED_OUTLINE;
        boolean blocked = hero_inside != inside;

        //range for CS check
        for (DC_Obj sub : list) {
//            game.getVisionMaster().getSightMaster().getClearShotCondition().preCheck(ref);

            if (blocked)
                assertTrue(sub.getOutlineType() == outline);
            else
                assertTrue(sub.getOutlineType() != outline);
            if (!cellsOrObjects)
                assertTrue(sub.getVisibilityLevelForPlayer() == VISIBILITY_LEVEL.UNSEEN);
        }
    }

    private List<? extends DC_Obj> getObjects(boolean inside, boolean cellsOrObjects) {
        List<Coordinates> coordinates = getCoordinatesList(inside);
        return null;
    }

    private List<Coordinates> getCoordinatesList(boolean inside) {
        List<Coordinates> list = new LinkedList<>();
        int x1 = (game.getDungeon().getCellsX() - getInnerWidth()) / 2;
        int y1 = (game.getDungeon().getCellsY() - getInnerHeight()) / 2;
        int x2 = x1 + getInnerWidth();
        int y2 = y1 + getInnerHeight();
        for (int x = x1; x < x2; x++)
            for (int y = y1; y < y2; y++) {
                if (isDiagonal()) {
                    int centerX = game.getDungeon().getCellsX() / 2;
                    int centerY = game.getDungeon().getCellsY() / 2;
                    int diffX = centerX - x;
                    int diffY = centerY - y;
//                    if (x<centerX && y<centerY && )
//                        continue;
                    if (x<centerX && y<centerY)
                        continue;
                }
                list.add(new Coordinates(x, y));
        }

        return list;
    }

    public void makeChecks() {
        //for all cells/objects outside, all statuses must be correct
        // then inside


    }
}
