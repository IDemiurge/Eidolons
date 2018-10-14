package eidolons.game.battlecraft.logic.meta.universal;

import eidolons.game.core.Eidolons;
import eidolons.game.module.dungeoncrawl.quest.QuestMaster;
import eidolons.libgdx.gui.panels.headquarters.town.TownPanel;
import eidolons.macro.FauxMacroGame;
import eidolons.macro.entity.MacroRef;
import eidolons.macro.entity.town.Town;
import main.content.enums.macro.MACRO_OBJ_TYPES;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.threading.WaitMaster;

/**
 * Created by JustMe on 10/13/2018.
 */
public class TownMaster extends MetaGameHandler {

    ShopManager shopManager;
    QuestMaster questMaster;
    private boolean inTown;
    private Town town;

    public TownMaster(MetaGameMaster master) {
        super(master);
        shopManager= createShopManager();
        questMaster = new QuestMaster();
    }

    private ShopManager createShopManager() {
        return new ShopManager(master);
    }
    //    LibraryManager

    public boolean initTownPhase() {
        try {
            town = getOrCreateTown();
            town.setQuests(questMaster.getQuestTypePool());
            Eidolons.getMainHero().reset();
            GuiEventManager.trigger(GuiEventType.SHOW_TOWN_PANEL, town);
            inTown = true;
            boolean result = (boolean) WaitMaster.waitForInput(TownPanel.DONE_OPERATION);
            inTown = false;
            return result;
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        return false;
    }

    public Town getTown() {
        return town;
    }

    private Town getOrCreateTown() {
        //        master.get
        ObjType type = DataManager.getType("Headquarters", MACRO_OBJ_TYPES.TOWN);
        return new Town(FauxMacroGame.getInstance(), type, MacroRef.getMainRef());
    }


    public boolean isInTown() {
        return inTown;
    }

    public QuestMaster getQuestMaster() {
        return questMaster;
    }
}
