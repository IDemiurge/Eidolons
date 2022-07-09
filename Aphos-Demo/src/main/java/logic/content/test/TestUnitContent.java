package logic.content.test;

import content.LinkedStringMap;
import main.data.StringMap;
import main.system.data.DataUnit;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

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

    public enum TestUnit implements ContentEnum {
        Zombie(50, 10, 2, 0, 5, 5, 3, 0, "units/Zombie.png"),
        Fiend(35, 15, 4, 0, 12, 6, 5, 0, "units/haunter.png"),
        Rogue(20, 12, 1, 0, 15, 12, 7, 0, "units/silent.png"), //will be a lot more... but we could pack it into modular structs
        Archer(20, 5, 1, 6, 10, 5, 6, 0, "main//undead//Zombie.png"),

        ;
        /*
        soundset
         */
        private final float hp;
        private final int armor;
        private final float initiative;
        private final float attack;
        private final float defense;
        private final float damage;
        private final float ranged;
        private final int aoe;
        private final String name;
        private final String image;
        private final Map<String, Object> values = new LinkedStringMap<>();

        TestUnit(float hp, float damage, int armor, float ranged, float attack, float defense, float initiative, int aoe, String image) {
            this.name = toString();
            this.hp = hp;
            this.armor = armor;
            this.initiative = initiative;
            this.attack = attack;
            this.defense = defense;
            this.damage = damage;
            this.ranged = ranged;
            this.aoe = aoe;
            this.image = image;
            init(this);
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

        @Override
        public Map<String, Object> getValues() {
            return values;
        }
    }
}
