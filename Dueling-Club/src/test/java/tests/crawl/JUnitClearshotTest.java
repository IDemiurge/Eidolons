package tests.crawl;

import main.ability.conditions.special.ClearShotCondition;
import main.content.enums.rules.VisionEnums.OUTLINE_TYPE;
import main.content.enums.rules.VisionEnums.VISIBILITY_LEVEL;
import main.entity.obj.DC_Obj;
import main.game.battlecraft.logic.battlefield.FacingMaster;
import main.game.bf.Coordinates;
import main.game.bf.Coordinates.FACING_DIRECTION;
import main.system.datatypes.DequeImpl;
import main.system.math.PositionMaster;
import org.junit.Test;
import tests.FastDcTest;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by JustMe on 10/26/2017.
 */
public class JUnitClearshotTest extends FastDcTest {

    protected List<Coordinates> insideCoords;
    protected List<Coordinates> outsideCoords;

    @Override
    public String getDungeonPath() {
        return "test\\clearshot test rectangle.xml";
    }

    public int getInnerWidth() {
        return 7;
    }

    public int getInnerHeight() {
        return 5;
    }

    public void check(boolean hero_inside, boolean inside, boolean cellsOrObjects) {
        DequeImpl<? extends DC_Obj> list = getObjects(inside, cellsOrObjects);
        OUTLINE_TYPE outline = OUTLINE_TYPE.BLOCKED_OUTLINE;
        boolean blocked = hero_inside != inside;

        //range for CS check
        for (DC_Obj sub : list) {
//            game.getVisionMaster().getSightMaster().getClearShotCondition().preCheck(ref);

            if (blocked)
                if (sub.getOutlineType() == outline)
                    assertTrue(true);
                else
                    assertTrue(game.getVisionMaster().getSightMaster().getClearShotCondition().check(getHero(), sub));
            else if (sub.getOutlineType() != outline)
                assertTrue(true);
            else
                assertTrue(false);

            if (!cellsOrObjects)
                if (!sub.isMine())
                    if (sub.getVisibilityLevelForPlayer() != VISIBILITY_LEVEL.UNSEEN)
                        assertTrue(false);
                    else
                        assertTrue(true);
        }
    }


    protected DequeImpl<? extends DC_Obj> getObjects(boolean inside, boolean cellsOrObjects) {
        DequeImpl<? extends DC_Obj> list = new DequeImpl<>();
        List<Coordinates> coordinates = getCoordinatesList(inside);
        for (Coordinates c : coordinates) {
            if (cellsOrObjects)
                list.addCast(game.getCellByCoordinate(c));
            else
                list.addAllCast(game.getObjectsAt(c));
        }

        return list;
    }

    protected List<Coordinates> getCoordinatesList(boolean inside) {
        if (inside)
            if (insideCoords != null)
                return insideCoords;
        if (outsideCoords != null)
            return outsideCoords;
        List<Coordinates> list = createCoordinatesList();

        if (!inside) {
            List<Coordinates> excluded = list;
            list = new LinkedList<>(game.getCoordinates());
            list.removeIf(c -> excluded.contains(c));
        }
        if (inside)
            insideCoords = filterCoordinates(list);
        else
            outsideCoords = filterCoordinates(list);
        return inside ? insideCoords : outsideCoords;
    }

    protected List<Coordinates> createCoordinatesList() {
        List<Coordinates> list = new LinkedList<>();
        int x1 = (game.getDungeon().getCellsX() - getInnerWidth()) / 2;
        int y1 = (game.getDungeon().getCellsY() - getInnerHeight()) / 2;
        int x2 = x1 + getInnerWidth();
        int y2 = y1 + getInnerHeight();
        for (int x = x1; x < x2; x++)
            for (int y = y1; y < y2; y++) {
                list.add(new Coordinates(x, y));
            }
        return list;
    }

    protected List<Coordinates> filterCoordinates(List<Coordinates> list) {
        list.removeIf(c ->
         !checkCoordinate(c)
        );
        list.removeIf(c -> PositionMaster.getExactDistance(game.getManager().getMainHero().getCoordinates(),
         c) > ClearShotCondition.getMaxCheckDistance(game.getManager().getMainHero(), c));
        return list;
    }

    //                if (isDiagonal()) {
//        int centerX = game.getDungeon().getCellsX() / 2;
//        int centerY = game.getDungeon().getCellsY() / 2;
//        int diffX = centerX - x;
//        int diffY = centerY - y;
////                    if (x<centerX && y<centerY && )
////                        continue;
//        if (x < centerX && y < centerY)
//            continue;
//    }

    protected boolean isInside(Coordinates c) {
        return getCoordinatesList(true).contains(c);
//        int y = (game.getBF_Height() - getInnerHeight()) / 2;
//        if (c.y < y)
//            return false;
//        if (c.y >= getInnerHeight() + y)
//            return false;
//        int x = (game.getBF_Width() - getInnerWidth()) / 2;
//        if (c.x < x)
//            return false;
//        if (c.x >= getInnerWidth() + x)
//            return false;
//        return true;
    }

    protected boolean checkCoordinate(Coordinates c) {
        if (game.getObjectByCoordinate(c) != null)
            return false;
        return true;
    }

    @Test
    public void makeChecks() {

        for (int x = 0; x < game.getBF_Width(); x++)
            for (int y = 0; y < game.getBF_Height(); y++) {
                Coordinates c = new Coordinates((x), y);
                if (!checkCoordinate(c))
                    continue;
                boolean heroInside = isInside(c);
                boolean checkInside = !heroInside;
                game.getManager().getMainHero().setCoordinates(c);
                for (FACING_DIRECTION sub : FacingMaster.FACING_DIRECTIONS) {
                    game.getManager().getMainHero().setFacing(sub);
                    game.getManager().reset();
//                    game.getMaster().clearCaches(); TODO optimized version?
//                    VisionManager.refresh();
                    check(heroInside, checkInside, true);
                    check(heroInside, checkInside, false);
                }
            }
        //for all cells/objects outside, all statuses must be correct
        // then inside


    }


}
