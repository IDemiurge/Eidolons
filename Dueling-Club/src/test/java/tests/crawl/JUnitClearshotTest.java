package tests.crawl;

import main.ability.conditions.special.ClearShotCondition;
import main.content.enums.rules.VisionEnums.OUTLINE_TYPE;
import main.content.enums.rules.VisionEnums.VISIBILITY_LEVEL;
import main.entity.obj.BattleFieldObject;
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
    protected boolean diagonal;

    @Override
    public String getDungeonPath() {
        if (isDiagonal()) {
            return "test\\clearshot test diagonal.xml";
        }
        return "test\\clearshot test rectangle.xml";
    }

    public int getInnerWidth() {
        return isDiagonal() ? 7 : 7;
    }

    public int getInnerHeight() {
        return isDiagonal() ? 7 : 5;
    }

    public void check(boolean hero_inside, boolean inside, boolean cellsOrObjects) {
        DequeImpl<? extends DC_Obj> list = getObjects(inside, cellsOrObjects);
        for (DC_Obj sub : list) {
            checkObj(sub, hero_inside, inside);
        }
    }

    protected void checkObj(DC_Obj sub, boolean hero_inside, boolean inside) {
        OUTLINE_TYPE outline = sub.getOutlineType();
        boolean blocked = hero_inside != inside;

        if (blocked)
            if (OUTLINE_TYPE.BLOCKED_OUTLINE != outline)
                assertTrue(game.getVisionMaster().getSightMaster().getClearShotCondition().check(getHero(), sub));
        else if (OUTLINE_TYPE.BLOCKED_OUTLINE == outline)
            assertTrue(false);

        if (sub instanceof BattleFieldObject)
            if (!sub.isMine())
                if (sub.getVisibilityLevelForPlayer() != VISIBILITY_LEVEL.UNSEEN)
                    assertTrue(false);
    }


    protected DequeImpl<? extends DC_Obj> getObjects(boolean inside, boolean cellsOrObjects) {
        DequeImpl<? extends DC_Obj> list = new DequeImpl<>();
        List<Coordinates> coordinates = getCoordinatesList(inside);
        coordinates = filterCoordinates(new LinkedList<>(coordinates));
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
            outsideCoords = list;
        } else insideCoords = list;

        return inside ? insideCoords : outsideCoords;
    }

    protected List<Coordinates> createCoordinatesList() {
        if (isDiagonal())
            return createDiagonalCoordinatesList();
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

    protected boolean isDiagonal() {
        return diagonal;
    }

    protected List<Coordinates> filterCoordinates(List<Coordinates> list) {
        list.removeIf(c ->
         !checkCoordinate(c)
        );
        list.removeIf(c -> PositionMaster.getExactDistance(game.getManager().getMainHero().getCoordinates(),
         c) >= ClearShotCondition.getMaxCheckDistance(game.getManager().getMainHero(), c));
        return list;
    }

    protected List<Coordinates> createDiagonalCoordinatesList() {
        List<Coordinates> list = new LinkedList<>();
        int yGap = game.getBF_Height() / 2 - (getInnerHeight() - 1) / 2;
        int i = -1;
        for (int y = yGap;
             y < game.getBF_Height() - y; y++) {
            i++;
            for (int x = game.getBF_Width() / 2 - i;
                 x <= game.getBF_Width() / 2 + i; x++) {
                list.add(new Coordinates(x, y));
            }
        }
        i = -1;
        for (int y = game.getBF_Height() - yGap - 1;
             y > game.getBF_Height() / 2; y--) {
            i++;
            for (int x = game.getBF_Width() / 2 - i;
                 x <= game.getBF_Width() / 2 + i; x++) {
                list.add(new Coordinates(x, y));
            }
        }
        return list;
    }

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


        checkClearshots(false);
        checkClearshots(true);
        diagonal = true;
        checkClearshots(false);
        checkClearshots(true);
    }

    public void checkClearshots(boolean breakMode) {
        ClearShotCondition.setUnitTestBreakMode(breakMode);
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
                    //game.getMaster().clearCaches(); TODO optimized version?
//                    VisionManager.refresh();
                    check(heroInside, checkInside, true);
                    check(heroInside, checkInside, false);
                }
            }
        //for all cells/objects outside, all statuses must be correct
        // then inside


    }


}
