package main.content.enums.entity;

/**
 * Created by JustMe on 11/16/2017.
 */
public class DungeonObjEnums {

    public enum CONTAINER_CONTENTS {
        AMMO(3), POTIONS(4), ARMOR, WEAPONS(2), FOOD(10), MISC(3), TOOLS(4),
        JUNK(5), TREASURE(2), JEWELRY(2);
        int maxItems ;
        
        CONTAINER_CONTENTS( ) {
            this(1);
        }
            CONTAINER_CONTENTS(int maxItems) {
            this.maxItems = maxItems;
        }

        public int getMaxItems() {
            return maxItems;
        }
    }

    public enum CONTAINER_CONTENT_VALUE {
        JUNK(100),
        COMMON(200),
        RARE(500),
        LEGENDARY(1500); // one item?
        int goldCost;

        public int getGoldCost() {
            return goldCost;
        }

        CONTAINER_CONTENT_VALUE(int goldCost) {
            this.goldCost = goldCost;
        }
    }

    public enum CONTAINER_TYPE {

    }
}
