package eidolons.game.module.dungeoncrawl.explore;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.module.herocreator.logic.HeroLevelManager;
import main.entity.Ref;
import main.entity.obj.Obj;
import main.game.logic.event.Event;
import main.game.logic.event.Event.STANDARD_EVENT_TYPE;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.RandomWizard;

/**
 * Created by JustMe on 9/9/2017.
 */
public class DungeonCrawler {


    public static void secretFound(Obj wall, Unit hero) {
        HeroLevelManager.addXp(hero, hero.getLevel() * 25 + 25 +
         RandomWizard.getRandomInt(100));
        GuiEventManager.trigger(GuiEventType.SHOW_INFO_TEXT,
         "A false wall? Interesting...");

        Ref ref = hero.getRef();
        ref.setTarget(wall.getId());
        hero.getGame().fireEvent(new Event(STANDARD_EVENT_TYPE.SECRET_FOUND, ref));
    }
}
