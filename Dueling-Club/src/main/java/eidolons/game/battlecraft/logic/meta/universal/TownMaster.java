package eidolons.game.battlecraft.logic.meta.universal;

import eidolons.game.core.Eidolons;
import eidolons.game.module.dungeoncrawl.quest.QuestMaster;
import eidolons.libgdx.gui.panels.headquarters.town.TownPanel;
import eidolons.macro.FauxMacroGame;
import eidolons.macro.entity.MacroRef;
import eidolons.macro.entity.town.Town;
import eidolons.system.audio.MusicMaster;
import eidolons.system.audio.MusicMaster.MUSIC_SCOPE;
import main.content.enums.macro.MACRO_OBJ_TYPES;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.launch.CoreEngine;
import main.system.threading.WaitMaster;

/**
 * Created by JustMe on 10/13/2018.
 */
public class TownMaster extends MetaGameHandler {

    private static final java.lang.String DEFAULT_TOWN = "Strangeville"; //"Headquarters"
    ShopManager shopManager;
    QuestMaster questMaster;
    private boolean inTown;
    private Town town;

    public TownMaster(MetaGameMaster master) {
        super(master);
        shopManager = createShopManager();
        questMaster = new QuestMaster();
        shopManager.init();
    }

    private ShopManager createShopManager() {
        return new ShopManager(master);
    }
    //    LibraryManager

    public boolean initTownPhase() {
        try {
            Town town = getOrCreateTown();
            town.setQuests(questMaster.getQuestsPool());
            return enterTown(town);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        return false;
    }

    public boolean enterTown(Town town) {
        if (this.town == null)
            try {
                Eidolons.getMainHero().reset();
            } catch (Exception e) {
                main.system.ExceptionMaster.printStackTrace(e);
            }
        this.town = town;
        GuiEventManager.trigger(GuiEventType.SHOW_TOWN_PANEL, town);
        inTown = true;
        if (CoreEngine.isFullFastMode()) {
            new Thread(() -> {
                //                try {
                //                    HqDataMasterDirect.getInstance().operation(HERO_OPERATION.BUY,
                //                     town.getShops().get(0).getItems().iterator().next());
                //                } catch (Exception e) {
                //                    main.system.ExceptionMaster.printStackTrace(e);
                //                }
                GuiEventManager.trigger(GuiEventType.QUEST_TAKEN, "To the Rescue");
                WaitMaster.WAIT(900);
                TownPanel.getActiveInstance().done();
            }, " thread").start();
        } else {
            MusicMaster.getInstance().scopeChanged(MUSIC_SCOPE.MAP);
        }
        boolean result =
         (boolean) WaitMaster.waitForInput(TownPanel.DONE_OPERATION);
        inTown = false;
        town.exited();
        return result;
    }

    public Town getTown() {
        return town;
    }

    private Town getOrCreateTown() {
        Town town = getGame().town; //TODO [refactor] !!!
        if (town != null) {
            return town;
        }
        ObjType type = DataManager.getType(DEFAULT_TOWN, MACRO_OBJ_TYPES.TOWN);
        town = new Town(new FauxMacroGame()
//         FauxMacroGame.getInstance() not safe?
         , type, MacroRef.getMainRef());
        getGame().town= town;
        return town; }


    public boolean isInTown() {
        return inTown;
    }

    public QuestMaster getQuestMaster() {
        return questMaster;
    }
}
