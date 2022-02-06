package eidolons.content.etalon;

import eidolons.content.ATTRIBUTE;
import eidolons.content.DC_ContentValsManager;
import main.content.enums.entity.ClassEnums;
import main.content.enums.entity.SkillEnums;
import main.system.auxiliary.StringMaster;
import main.system.data.DataUnit;

public class EtalonConsts {

    public enum EtalonType {
        //TODO use combinatorics for race!
        fighter_defense("Human", 6, ClassEnums.CLASS_GROUP.FIGHTER, MASTERY_SET.fighter_defense, ATTRIBUTE_SET.fighter_defense),
        fighter_offense("Human",6, ClassEnums.CLASS_GROUP.FIGHTER, MASTERY_SET.fighter_offense, ATTRIBUTE_SET.fighter_offense),
        fighter_offense_alt("Human",6, ClassEnums.CLASS_GROUP.FIGHTER, MASTERY_SET.fighter_offense_alt, ATTRIBUTE_SET.fighter_offense_alt),
        rogue_offense("Human",6, ClassEnums.CLASS_GROUP.ROGUE, MASTERY_SET.rogue_offense, ATTRIBUTE_SET.rogue_offense),
        rogue_specialist("Human",6, ClassEnums.CLASS_GROUP.ROGUE, MASTERY_SET.rogue_specialist, ATTRIBUTE_SET.rogue_specialist),
        ;

        public int maxLevel;
        public String name;
        public String baseType;
        ClassEnums.CLASS_GROUP baseClass;
        EtalonConsts.MASTERY_SET masterySet;
        ATTRIBUTE_SET attributeSet;

        EtalonType(String race, int maxLevel, ClassEnums.CLASS_GROUP baseClass, MASTERY_SET masterySet, ATTRIBUTE_SET attributeSet) {
            this.maxLevel = maxLevel;
            this.baseClass = baseClass;
            this.masterySet = masterySet;
            this.attributeSet = attributeSet;
            name = StringMaster.format(name());
            baseType = "Base Type "+ race;
        }
    }

    //to get beyond lvl6, apply TierII mastery_set? Warrior_Offense etc?

    public enum MASTERY_SET {
        fighter_defense(0.85f, "blade:10;defense:10;shield:8;athletics:7;mobility:5"),
        fighter_offense(0.85f, "axe:10;blunt:10;athletics:8;two_handed:7;mobility:5"),
        fighter_offense_alt(0.85f, "polearm:10;blade:10;athletics:8;two_handed:7;mobility:5"),
        rogue_offense(0.7f, "blade:10;mobility:8;dual wielding:6;stealth:5;athletics:4;"),
        rogue_specialist(0.75f, "item:8;blade:6;mobility:8;dual_wielding:5;stealth:8;"),
        ;

        public float spentCoef;

        MASTERY_SET(float v, String weights) {
            this.spentCoef = v;
            this.weights = weights;
        }

        public String weights; // alter at level X?

    }

    public enum ATTRIBUTE_SET {
        fighter_defense(0.85f,
                "Strength:10;Vitality:12;Agility:6;Dexterity:7;Willpower:7;" +
                        "Intelligence:2;Knowledge:1;Spellpower:1;Wisdom:2;Charisma:1;"),
        fighter_offense(0.85f,
                "Strength:12;Vitality:10;Agility:8;Dexterity:6;Willpower:6;" +
                        "Intelligence:2;Knowledge:1;Spellpower:1;Wisdom:2;Charisma:1;"),
        fighter_offense_alt(0.85f,
                "Strength:12;Vitality:10;Agility:8;Dexterity:6;Willpower:6;" +
                        "Intelligence:2;Knowledge:1;Spellpower:1;Wisdom:2;Charisma:1;"),
        rogue_offense(0.7f,
                "Strength:7;Vitality:6;Agility:11;Dexterity:9;Willpower:5;" +
                        "Intelligence:3;Knowledge:1;Spellpower:1;Wisdom:1;Charisma:2;"),
        rogue_specialist(0.75f,
                "Strength:6;Vitality:6;Agility:11;Dexterity:9;Willpower:5;" +
                        "Intelligence:5;Knowledge:1;Spellpower:1;Wisdom:1;Charisma:4;"),
        ;

        public float spentCoef;

        ATTRIBUTE_SET(float v, String weights) {
            this.spentCoef = v;
            this.weights = weights;
        }

        public String weights; // alter at level X?

    }

    public static class AttributeData extends DataUnit<ATTRIBUTE> {
        @Override
        public Class<? extends ATTRIBUTE> getEnumClazz() {
            return ATTRIBUTE.class;
        }

        public AttributeData(String text) {
            super(text);
        }
    }

    public static class MasteryData extends DataUnit<SkillEnums.MASTERY> {
        @Override
        public Class<? extends SkillEnums.MASTERY> getEnumClazz() {
            return SkillEnums.MASTERY.class;
        }

        @Override
        public SkillEnums.MASTERY getEnumConst(String string) {
            return super.getEnumConst(string + DC_ContentValsManager.MASTERY);
        }

        @Override
        public String getValue(SkillEnums.MASTERY mastery) {
            return super.getValue(mastery.toString().replace(DC_ContentValsManager.MASTERY, ""));
        }

        public MasteryData(String text) {
            super(text);
        }
    }
}
