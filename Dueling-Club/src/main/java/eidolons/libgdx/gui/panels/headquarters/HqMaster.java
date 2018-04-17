package eidolons.libgdx.gui.panels.headquarters;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.game.DC_Game;
import eidolons.libgdx.gui.panels.headquarters.datasource.HqDataMaster;
import eidolons.libgdx.gui.panels.headquarters.datasource.hero.HqHeroDataSource;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 4/16/2018.
 */
public class HqMaster {

    public static void closeHqPanel(){
        GuiEventManager.trigger(GuiEventType.SHOW_HQ_SCREEN, null );
    }
        public static void openHqPanel(){

         List<Unit> members= new ArrayList<>();
        members.add(DC_Game.game.getManager().getMainHero());
        List<HqHeroDataSource> list = new ArrayList<>();

        for (Unit sub : members) {
            list.add(new HqHeroDataSource(new HqDataMaster(
             sub).getHeroModel()));

        }
        GuiEventManager.trigger(GuiEventType.SHOW_HQ_SCREEN, list);


    }

    public static void toggleHqPanel() {
        if (HqPanel.getActiveInstance() != null) {
            closeHqPanel();
        } else {
            openHqPanel();
        }

    }
}
