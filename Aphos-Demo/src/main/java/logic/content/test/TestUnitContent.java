package logic.content.test;

import content.LinkedStringMap;
import logic.content.AUnitEnums;
import main.data.StringMap;
import main.system.auxiliary.StringMaster;
import main.system.data.DataUnit;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

import static logic.content.AUnitEnums.*;
import static logic.content.AUnitEnums.UnitType.*;

public class TestUnitContent {

    public enum TestHero implements ContentEnum {
        Elberen(TestUnit.Rogue, "image:heroes/gwyn token.png;"),
        ;
        private Map<String, Object> map;

        TestHero(String stringMap, TestUnit... template) {
            //add up?
        }

        TestHero(TestUnit template, String stringMap) {
            this.map = new LinkedStringMap<>();
            this.map.putAll(template.getValues());
            this.map.putAll(new DataUnit<>(stringMap).getValues());
            // parseStringMap(stringMap)
        }

        @Override
        public Map<String, Object> getValues() {
            return map;
        }
    }
public static void initAbils(){
//        TestUnit.Lancer.abil("");
}
    public enum TestUnit implements ContentEnum {
        //will be a lot more... but we could pack it into modular structs - such as WEAPON, MAGIC
        /*            hp|damage|armor|initiative|attack|defense|ranged|   aoe |         */
        Fiend        (35,  13,     7,      13,      16,    11,     0,      0, Melee, ""),
        Lancer       (30,  12,     10,      11,      12,    14,     1,      0, Melee, ""),
        Rogue        (25,  10,     3,      15,      15,     5,     1,      0, Sneak, ""),
//        Silent        (25,  10,     3,      15,      15,     5,     1,      0, Sneak, ""),
        Archer       (15,  8,     5,      10,      12,     5,     3,      0, Ranged, "", val("body", Body.bone)),
//        Deepeye       (15,  8,     5,      10,      12,     5,     3,      0, Ranged, "", val("body", Body.bone)),
        Zombie       (50,  10,     6,      6,      5,      5,      0,      0, Guard, ""),
        Golem        (25,  12,     15,      5,      10,     10,    0,      0, Guard, "", val("body", Body.metal)),
        Haunter      (18,  6,     4,      12,      10,     8,     2,      1, Caster, "", val("body", Body.dust)),
//        Alchemist      (18,  6,     4,      12,      10,     8,     2,      1, Caster, "", val("body", Body.dust)),
        Bull        (75,  10,      0,      8,        6,      2,    0,      1, Explode, "", val("explode", 40)),
//        Mother        (75,  10,      0,      8,        6,      2,    0,      1, Explode, "", val("explode", 40)),
        //Knight multi


        ;













        private final UnitType type;
        private final int hp,armor, initiative, attack, defense, damage, ranged, aoe;
        public final String name;
        private final String image;

        private final Map<String, Object> values = new LinkedStringMap<>();

        TestUnit( int hp, int damage, int armor, int initiative,int attack, int defense,  int ranged, int aoe, UnitType type,String image,
                 String... additional) {
            this.type = type;
            this.name = toString();
            this.hp = hp;
            this.armor = armor;
            this.initiative = initiative;
            this.attack = attack;
            this.defense = defense;
            this.damage = damage;
            this.ranged = ranged;
            this.aoe = aoe;
            if (image.isEmpty())
                image = "units/"+name().toLowerCase() +".png";
            this.image = image;
            init(this);
            initAdditional(additional);
        }


        private void init(TestUnit testUnit) {
            for (Field field : testUnit.getClass().getDeclaredFields()) {
                if (!field.getName().equals("values")) {
                    field.setAccessible(true);
                    Object o = null;
                    try {
                        o = field.get(testUnit);
                        testUnit.values.put(field.getName().toUpperCase(), o);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        private void initAdditional(String[] additional) {
            for (String s : additional) {
                String[] split = s.split(":");
                values.put(split[0].toUpperCase(), split[1]);
            }
        }

        private static String val(String name, Object o) {
            return name+":" + o.toString();
        }
        @Override
        public Map<String, Object> getValues() {
            return values;
        }
    }
}
