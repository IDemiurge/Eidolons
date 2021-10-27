package libgdx.gui.dungeon.overlay.choice;

import eidolons.game.battlecraft.logic.meta.universal.MetaGameHandler;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;

public class VisualChoiceHandler extends MetaGameHandler {

    private static VisualChoiceHandler instance;

    public VisualChoiceHandler(MetaGameMaster master) {
        super(master);
        instance = this;
    }

    public static boolean checkOptionDisabled(VC_DataSource.VC_OPTION option) {
       return  instance.isDisabled(option);
    }

    public static boolean isOn() {
        return false;
    }

    public   boolean isDisabled(VC_DataSource.VC_OPTION option) {
        return false;
    }

}
