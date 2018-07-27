package tests.gdx;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.bf.grid.BaseView;
import eidolons.libgdx.bf.grid.GridCellContainer;
import eidolons.libgdx.screens.DungeonScreen;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.game.logic.battle.player.Player;
import main.system.auxiliary.NumberUtils;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by JustMe on 2/12/2018.
 */
public class GdxUnitPosSizeTest extends LibgdxTest {

    private static final int X = 0;
    private static final int Y = 0;
    private static final String UNIT_TYPE = "Spiderling";
    private Player player;
    private GridCellContainer container;

    protected String getDungeonPath() {
        return "test\\empty.xml";
    }

    protected String getPlayerParty() {
        return null;
    }

    protected String getEnemyParty() {
        return "1-1=Spiderling;1-1=Spiderling;1-1=Spiderling;1-1=Spiderling;1-1=Spiderling;1-1=Spiderling;";
    }

    @Test
    public void test() {
        WaitMaster.waitForInput(WAIT_OPERATIONS.DUNGEON_SCREEN_READY);
        player = DC_Game.game.getPlayer(true);
        for (Unit unit : DC_Game.game.getUnits()) {
            BaseView component =
             DungeonScreen.getInstance().getGridPanel().getViewMap().get(unit);
            container = (GridCellContainer) component.getParent();

            container.recalcUnitViewBounds();
            int index = 0;
            for (BaseView sub : container.getUnitViews(true)) {
                checkComponent(sub, container.getUnitViews(true).size()
//                  + 2
                 , index);
                index++;
            }
        }
    }

    private void checkComponent(BaseView component, int n, int i) {
        int offset = container.getUnitViewOffset();
        int size = (int) container.getUnitViewSize();
//        container.getUnitViewsVisible()
        String expected = NumberUtils.getOrdinal(i) + " view of " + n
         + " must have "
         + container.getViewX(offset, i) + " x; "
         + container.getViewY(offset, i, n) + " y; "
         + size + " width; "
         + size + " height; ";
        String actual = component
         + " has "
         + component.getX() + " x; "
         + component.getY() + " y; "
         + component.getWidth() * component.getScaledHeight() + " width; "
         + component.getHeight() * component.getScaledHeight() + " height; ";

        main.system.auxiliary.log.LogMaster.log(1, expected
        );
        main.system.auxiliary.log.LogMaster.log(1, actual
        );

        assertTrue(expected + "\n" + actual, component.getHeight() * component.getScaledHeight() == size);
        assertTrue(expected + "\n" + actual, component.getWidth() * component.getScaledWidth() == size);
        assertTrue(expected + "\n" + actual, component.getX() == container.getViewX(offset, i));
        assertTrue(expected + "\n" + actual, component.getY() == container.getViewY(offset, i, n));
    }

    private BattleFieldObject getUnit() {
        return (BattleFieldObject) DC_Game.game.getManager().getObjCreator().createUnit(getType(),
         X, Y, player, new Ref());
    }

    private ObjType getType() {
        return DataManager.getType(UNIT_TYPE, DC_TYPE.UNITS);
    }

}
