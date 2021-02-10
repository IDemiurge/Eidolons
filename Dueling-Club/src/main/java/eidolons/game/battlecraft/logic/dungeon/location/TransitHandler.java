package eidolons.game.battlecraft.logic.dungeon.location;

import eidolons.game.battlecraft.logic.dungeon.universal.DungeonHandler;
import eidolons.game.battlecraft.logic.dungeon.universal.DungeonMaster;
import eidolons.game.core.EUtils;
import eidolons.game.core.Eidolons;
import eidolons.game.module.dungeoncrawl.dungeon.Entrance;
import main.game.bf.Coordinates;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.threading.WaitMaster;

/**
 * Created by JustMe on 5/8/2017.
 */
public class TransitHandler extends DungeonHandler {

    public TransitHandler(DungeonMaster master) {
        super(master);
    }

    private boolean confirmExit() {
//        if (CoreEngine.isIggDemo()) {
//            return true;
//        }
        EUtils.onConfirm("Leave this location? " +
                        "Don't forget to check your achievements from the main menu!", () ->
                        WaitMaster.receiveInput(WaitMaster.WAIT_OPERATIONS.CONFIRM, true),
                () ->
                        WaitMaster.receiveInput(WaitMaster.WAIT_OPERATIONS.CONFIRM, false));
        return (boolean) WaitMaster.waitForInput(WaitMaster.WAIT_OPERATIONS.CONFIRM);
    }

    public boolean checkNextFloor() {
        Coordinates c = getGame().getManager().getMainHeroCoordinates();
        if (master.getFloorWrapper() instanceof Location) {
            Location location = getGame().getDungeonMaster().getFloorWrapper();
            if (location.getMainExit() != null) {
                if (location.getMainExit().getCoordinates().equals(c)) {
                    return true;
                }
            }
            //TODO support retreat?!
            for (Entrance transit : location.getTransits()) {
                if (transit.getCoordinates().equals(c)) {
                    if (!confirmExit()) {
                        return false;
                    }
                    transit(transit);
                    break;
                }
            }
        }
        return false;
    }

    private void transit(Entrance transit) {
        Eidolons.getMainHero().setCoordinates(transit.getTargetCoordinates());
        getModuleLoader().loadModuleFull(transit.getTargetModule());
        WaitMaster.waitForInput(WaitMaster.WAIT_OPERATIONS.GUI_READY);
        GuiEventManager.trigger(GuiEventType.UNIT_MOVED, Eidolons.getMainHero());
        GuiEventManager.trigger(GuiEventType.CAMERA_PAN_TO_COORDINATE, Eidolons.getPlayerCoordinates());
    }
}
