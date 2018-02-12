package tests.gdx;

import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.Ref;
import main.entity.obj.BattleFieldObject;
import main.entity.type.ObjType;
import main.game.core.game.DC_Game;
import main.game.logic.battle.player.Player;
import main.libgdx.bf.BaseView;
import main.libgdx.bf.GridCellContainer;
import main.libgdx.screens.DungeonScreen;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StringMaster;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 2/12/2018.
 */
public class GdxUnitPosSizeTest extends GdxJUnit {

    private static final int X = 0;
    private static final int Y = 0;
    private static final String UNIT_TYPE = "Pirate";
    private Player player;
    private GridCellContainer container;

    @Test
    public void test() {
        WaitMaster.waitForInput(WAIT_OPERATIONS.DUNGEON_SCREEN_READY);
        player = DC_Game.game.getPlayer(true);
        BattleFieldObject unit = getUnit();
        GuiEventManager.trigger(GuiEventType.UNIT_CREATED, unit);
        WaitMaster.WAIT(100);
        BaseView component = DungeonScreen.getInstance().getGridPanel().getUnitMap().get(unit);

        container = (GridCellContainer) component.getParent();
        checkComponent(component, 1, 0);
        List<BaseView> components = new ArrayList<>();
        components.add(component);

        for (int i = 0; i < 10; i++) {
            unit = getUnit();
            GuiEventManager.trigger(GuiEventType.UNIT_CREATED, unit);
            WaitMaster.WAIT(100);
            component =
             DungeonScreen.getInstance().getGridPanel().getUnitMap().get(unit);
            components.add(component);
            int index = 0;
            for (BaseView sub : components) {
                checkComponent(sub, i + 2, index);
                index++;
            }
        }
    }

    private void checkComponent(BaseView component, int n, int i) {
        int offset = container.getUnitViewOffset();
        int size = (int) container.getUnitViewSize();
//        container.getUnitViewsVisible()
        main.system.auxiliary.log.LogMaster.log(1, StringMaster.getOrdinal(i) + " view of " + n
         + " must have "
         + container.getViewX(offset, i) + " x; "
         + container.getViewY(offset, i) + " y; "
         + size + " width; "
         + size + " height; "
        );

        main.system.auxiliary.log.LogMaster.log(1,
         component
          + " has "
          + container.getX() + " x; "
          + container.getY() + " y; "
          + container.getWidth() + " width; "
          + container.getHeight() + " height; "
        );

        assert (component.getWidth() == size);
        assert (component.getHeight() == size);
        assert (component.getX() == container.getViewX(offset, i));
        assert (component.getY() == container.getViewY(offset, i));
    }

    private BattleFieldObject getUnit() {
        return (BattleFieldObject) DC_Game.game.getManager().getObjCreator().createUnit(getType(),
         X, Y, player, new Ref());
    }

    private ObjType getType() {
        return DataManager.getType(UNIT_TYPE, DC_TYPE.UNITS);
    }

}
