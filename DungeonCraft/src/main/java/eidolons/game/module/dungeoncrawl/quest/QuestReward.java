package eidolons.game.module.dungeoncrawl.quest;

import eidolons.content.PARAMS;
import eidolons.content.consts.VisualEnums;
import eidolons.entity.item.DC_HeroItemObj;
import eidolons.entity.item.ItemFactory;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.EUtils;
import eidolons.game.core.Eidolons;
import eidolons.game.module.herocreator.logic.HeroLevelManager;
import eidolons.system.libgdx.GdxStatic;
import main.content.C_OBJ_TYPE;
import main.content.enums.meta.QuestEnums.QUEST_LEVEL;
import main.content.enums.meta.QuestEnums.QUEST_REWARD_TYPE;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.data.DataUnit;
import main.system.math.Formula;

/**
 * Created by JustMe on 10/5/2018.
 */
public class QuestReward extends DataUnit<QuestReward.REWARD_VALUE> {
    QUEST_REWARD_TYPE type;
    QUEST_LEVEL level;
    String goldFormula = "";
    String gloryFormula = "";
    String reputationFormula = "";
    String soulforceFormula = "";
    String rewardItems = "";
    //    private QUEST_REWARD_TYPE type;
//    private QUEST_LEVEL level;
    private String title;

    public enum REWARD_VALUE {
        xpFormula,
        goldFormula,
        gloryFormula,
        reputationFormula,
        soulforceFormula,
        rewardItems,
    }

    public QuestReward(String text) {
        super(text);
    }

    public QuestReward(ObjType objType) {
          type = new EnumMaster<QUEST_REWARD_TYPE>().retrieveEnumConst(QUEST_REWARD_TYPE.class,
                objType.getProperty(G_PROPS.QUEST_REWARD_TYPE));
          level = new EnumMaster<QUEST_LEVEL>().retrieveEnumConst(QUEST_LEVEL.class,
                objType.getProperty(G_PROPS.QUEST_LEVEL));


        int gold = 20 * Eidolons.getMainHero().getLevel();
        int xp = 20 * Eidolons.getMainHero().getLevel();
        int reputation = 10;

        if (type == null) {
            xp += 50;
            gold += 50;
        } else {
            switch (type) {
                case ITEM:
                case RANDOM:
                case RANK:
                    break;
                case XP:
                    xp += 100;
                    break;
                case GOLD:
                    gold += 100;
                    break;
                case MIXED:
                    xp += 50;
                    gold += 50;
                    break;
                case ANTI_GLORY:
                    gloryFormula = "-450";
                    break;
                case GLORY:
//               TODO      setValue(REWARD_VALUE.gloryFormula, "150");
                    gloryFormula = "150";
                    break;
            }
        }
        if (level != null) {
            xp = Math.round(xp * level.factor);
            gold = Math.round(gold * level.factor);
            reputation = Math.round(reputation * level.factor);
        }
        float r = RandomWizard.getRandomFloatBetween(0.6f, 1.4f);
        xp = Math.round(xp * r);
        gold = Math.round(gold * (1 / r));

        xp = xp - xp % 5;
        gold = gold - gold % 5;

        goldFormula = "" + gold;
        reputationFormula = "" + reputation;
        rewardItems = objType.getProperty(G_PROPS.QUEST_REWARD_ITEMS);
    }

    @Override
    public String toString() {
        if (type==QUEST_REWARD_TYPE.NONE) {
            return "";
        }
        String s = "Reward: \n";
        if (type==QUEST_REWARD_TYPE.SOULS) {
            s+=" Souls: 10";
            return s;
        }
        if (!goldFormula.isEmpty())
            s += " Gold: " + new Formula(goldFormula).getInt();
        if (!rewardItems.isEmpty())
            s += " Item: " + rewardItems;
        if (!reputationFormula.isEmpty())
            s += " Reputation: " + new Formula(reputationFormula).getInt();
        return s;
    }

    public void award(Unit hero, boolean inTown) {
        award(hero, inTown, false);
    }
        public void award(Unit hero, boolean inTown, boolean negative) {

        //TODO special menu with congrats?

        EUtils.showInfoText(title + " is complete!");

//        switch (level) {
//           TODO  case EASY:
//                DC_SoundMaster.playStandardSound(STD_SOUNDS.DIS__BOON_SMALL);
//                break;
//            case AVERAGE:
//                DC_SoundMaster.playStandardSound(STD_SOUNDS.DIS__BLESS);
//                break;
//            case HARD:
//                DC_SoundMaster.playStandardSound(STD_SOUNDS.DIS__BOON_LARGE);
//                break;
//        }
        Integer gold = new Formula(goldFormula).getInt(hero.getRef());
        Integer glory = new Formula(gloryFormula).getInt(hero.getRef());

            if (negative) {
                gold = -gold;
                glory = -glory;
//             TODO    glory = -glory;
            }

        hero.getGame().getMissionMaster().getStatManager().addGlory(glory);

        if (inTown) {
             addGold(hero, gold);
            for (String s : ContainerUtils.openContainer(rewardItems)) {
                if (DataManager.isTypeName(s, C_OBJ_TYPE.ITEMS)) {
                    DC_HeroItemObj item = ItemFactory.createItemObj(DataManager.getType(s,
                            C_OBJ_TYPE.ITEMS), hero, false);
                    hero.addItemToInventory(item);
                    EUtils.showInfoText("Added to inventory: " + item);
                }
            }
        }
    }

    public  void addGold(Unit hero, int gold) {
        hero.modifyParameter(PARAMS.GOLD, gold);
        GdxStatic.floatingText( VisualEnums.
                TEXT_CASES.GOLD, gold + " gold", hero);
        EUtils.showInfoText("Gold gained: " + gold);
    }
    public String getTitle() {
        return title;
    }


    public String getReputationFormula() {
        return reputationFormula;
    }

    public String getRewardItems() {
        return rewardItems;
    }

    public String getGoldFormula() {
        return goldFormula;
    }

    public int getReputationImpactComplete() {
        return new Formula(reputationFormula).getInt();
    }
}
