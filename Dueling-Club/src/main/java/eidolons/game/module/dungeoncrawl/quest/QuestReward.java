package eidolons.game.module.dungeoncrawl.quest;

import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.item.ItemFactory;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.EUtils;
import eidolons.game.core.Eidolons;
import eidolons.game.module.herocreator.logic.HeroLevelManager;
import eidolons.system.audio.DC_SoundMaster;
import main.content.C_OBJ_TYPE;
import main.content.enums.meta.QuestEnums.QUEST_LEVEL;
import main.content.enums.meta.QuestEnums.QUEST_REWARD_TYPE;
import main.content.values.properties.MACRO_PROPS;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.math.Formula;
import main.system.sound.SoundMaster.STD_SOUNDS;

/**
 * Created by JustMe on 10/5/2018.
 */
public class QuestReward {
    private   QUEST_REWARD_TYPE type;
    private   QUEST_LEVEL level;
    private   String title;
    String xpFormula="";
    String goldFormula="";
    String reputationFormula="";
    String itemDescriptor="";

    public QuestReward(ObjType objType) {
          type =
         new EnumMaster<QUEST_REWARD_TYPE>().retrieveEnumConst(QUEST_REWARD_TYPE.class,
          objType.getProperty(MACRO_PROPS.QUEST_REWARD_TYPE));
          level =
         new EnumMaster<QUEST_LEVEL>().retrieveEnumConst(QUEST_LEVEL.class,
          objType.getProperty(MACRO_PROPS.QUEST_LEVEL));
        int gold=20*Eidolons.getMainHero().getLevel();
        int xp=20*Eidolons.getMainHero().getLevel();

        if (type == null) {
            xp+=50;
            gold+=50;
        } else
        switch (type) {
            case ITEM:
                break;
            case RANK:
                break;
            case XP:
                xp+=100;
                break;
            case GOLD:
                gold+=100;
                break;
            case MIXED:
                xp+=50;
                gold+=50;
                break;
            case RANDOM:
                break;
        }
        if (level != null) {
        xp=Math.round(xp*level.factor);
        gold=Math.round(gold*level.factor);
        }
        float r = RandomWizard.getRandomFloatBetween(0.6f, 1.4f);
        xp=Math.round(xp*r);
        gold=Math.round(gold*(1/r));

        xp = xp-xp%5;
        gold = gold-gold%5;

        goldFormula=""+gold;
        xpFormula=""+xp;
    }

    @Override
    public String toString() {
        String s = "Reward: \n";
        if (!goldFormula.isEmpty())
            s+=" Gold: " + new Formula(goldFormula).getInt();
        if (!xpFormula.isEmpty())
            s+=" Experience: " + new Formula(xpFormula).getInt();
        if (!itemDescriptor.isEmpty())
            s+=" Item: " + itemDescriptor;
        if (!reputationFormula.isEmpty())
            s+=" Reputation: " + new Formula(reputationFormula).getInt();
        return s;
    }

    public void award(Unit hero, boolean inTown) {

        //TODO special menu with congrats?

        EUtils.showInfoText(title + " is complete!"  );

        switch (level) {
            case EASY:
                DC_SoundMaster.playStandardSound(STD_SOUNDS.DIS__BOON_SMALL);
                break;
            case AVERAGE:
                DC_SoundMaster.playStandardSound(STD_SOUNDS.DIS__BLESS);
                break;
            case HARD:
                DC_SoundMaster.playStandardSound(STD_SOUNDS.DIS__BOON_LARGE);
                break;
        }
        Integer xp = new Formula(xpFormula).getInt(hero.getRef());
        Integer gold = new Formula(goldFormula).getInt(hero.getRef());

        if (!inTown)
            HeroLevelManager.addXp(hero, xp);
        if (inTown)
        {
            HeroLevelManager.addGold(hero, gold);
        if (DataManager.isTypeName(itemDescriptor, C_OBJ_TYPE.ITEMS) ) {
            DC_HeroItemObj item = ItemFactory.createItemObj(DataManager.getType(itemDescriptor,
             C_OBJ_TYPE.ITEMS), hero, false);
             hero.addItemToInventory(item);
            EUtils.showInfoText( "Added to inventory: " + item  );
        }
        }
    }

    public QUEST_REWARD_TYPE getType() {
        return type;
    }

    public QUEST_LEVEL getLevel() {
        return level;
    }

    public String getTitle() {
        return title;
    }

    public String getXpFormula() {
        return xpFormula;
    }

    public String getReputationFormula() {
        return reputationFormula;
    }

    public String getItemDescriptor() {
        return itemDescriptor;
    }

    public String getGoldFormula() {
        return goldFormula;
    }
}
