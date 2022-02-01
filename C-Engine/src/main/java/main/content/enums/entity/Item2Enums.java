package main.content.enums.entity;

public class Item2Enums {
    public interface IMaterial {
        int getPower();
        int getLightweight();
        int getMagic();
    }
//TODO cloth?? Leather?
// Stone + Crystal
    // how far can we go in visual customization then? Color scheme, what else?

    // DO NOT REORDER CONSTS!
    public enum Metal implements IMaterial {
        Bronze(2, 4, 3),Iron(2, 4, 3),Steel(2, 4, 3),
        Mithril(2, 4, 3),Adamantium(2, 4, 3),Meteorite(2, 4, 3),
        Aurum(2, 4, 3),Silverite(2, 4, 3),// ???
        ;

        Metal(int power, int lightweight, int magic) {
            this.power = power;
            this.lightweight = lightweight;
            this.magic = magic;
        }

        int power, lightweight, magic;

        public int getPower() {
            return power;
        }

        public int getLightweight() {
            return lightweight;
        }

        public int getMagic() {
            return magic;
        }
    }

    // DO NOT REORDER CONSTS!
    public enum Wood implements IMaterial {
         //uses - bows/crossbows(2, 4, 3),staves, spears?   elven armor, highlander,..  clubs,
        Walnut(2, 4, 3),Red_Oak(2, 4, 3),Ebony(2, 4, 3),//bamboo?
        Palewood(2, 4, 3),Waxwood(2, 4, 3),Lotuswood(2, 4, 3),
        Bilewood(2, 4, 3),Moonwood(2, 4, 3),Blackwood(2, 4, 3)// ???
        ;

        Wood(int power, int lightweight, int magic) {
            this.power = power;
            this.lightweight = lightweight;
            this.magic = magic;
        }

        int power, lightweight, magic;

        public int getPower() {
            return power;
        }

        public int getLightweight() {
            return lightweight;
        }

        public int getMagic() {
            return magic;
        }
    }

    public enum Bone implements IMaterial {
        //usage - spears, arrows (?), wands, mage-armor, claws,
        Manbone(2, 4, 3), Oxenhorn(2, 4, 3), Steel(2, 4, 3),
        Mithril(2, 4, 3), Trollbone(2, 4, 3), Dragonbone(2, 4, 3),
        Aurum(2, 4, 3), Silverite(2, 4, 3), Demonbone(2, 4, 3) // ???
        ;

        Bone(int power, int lightweight, int magic) {
            this.power = power;
            this.lightweight = lightweight;
            this.magic = magic;
        }

        int power, lightweight, magic;

        public int getPower() {
            return power;
        }

        public int getLightweight() {
            return lightweight;
        }

        public int getMagic() {
            return magic;
        }
    }

    //TODO Content missing - cloth, stone, ...
}
