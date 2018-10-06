package eidolons.game.module.dungeoncrawl.quest;

import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.item.ItemFactory;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.EUtils;
import eidolons.game.module.herocreator.logic.HeroLevelManager;
import main.content.C_OBJ_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.math.Formula;

/**
 * Created by JustMe on 10/5/2018.
 */
public class QuestReward {
    private final String title;
    String xpFormula;
    String goldFormula;
    String itemDescriptor;

    @Override
    public String toString() {
        String s = "Reward: ";
        return super.toString();
    }

    public QuestReward(ObjType type, DungeonQuest quest) {
        title = quest.getTitle();
    }

    public void award(Unit hero) {

        //TODO special menu with congrats?

        EUtils.showInfoText(title + " is complete!"  );

        Integer xp = new Formula(xpFormula).getInt(hero.getRef());
        Integer gold = new Formula(goldFormula).getInt(hero.getRef());

        HeroLevelManager.addXp(hero, xp);
        HeroLevelManager.addGold(hero, gold);

        if (itemDescriptor != null) {
            DC_HeroItemObj item = ItemFactory.createItemObj(DataManager.getType(itemDescriptor,
             C_OBJ_TYPE.ITEMS), hero, false);
             hero.addItemToInventory(item);

            EUtils.showInfoText( "Added to inventory: " + item  );
        }

    }
}
