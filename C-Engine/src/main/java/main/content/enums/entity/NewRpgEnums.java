package main.content.enums.entity;

import main.system.auxiliary.StringMaster;

public class NewRpgEnums {

    public enum FeatSpaceValue {
        name,
        type,
        mode,
            feats,
        skin,
        index,
            slot_1,
            slot_2,
            slot_3,
            slot_4,
            slot_5,
            slot_6,
        ;

    }
    public enum FeatSpaceSkin {
        lite, //all kinds for grimoires!
    }

    public enum FeatSpaceMode {
        normal, auto, triggered,
    }

    public enum FeatSpaceType {
        training(1), quickItems(12),
        memorized(2), verbatim(1), divined(5),
        grimoire(3), sphere(4), song(6), custom(10);

            FeatSpaceType(int sortIndex) {
                this.sortIndex = sortIndex;
            }

            public int sortIndex;
        }

    public enum PerkValue {
        perks_1,
        quirks_1,
        add_perks_1,
        perks_2,
        quirks_2,
        add_perks_2,
        perks_3,
        quirks_3,
        add_perks_3,
        perks_4,
        quirks_4,
        add_perks_4,
        perks_5,
        quirks_5,
        add_perks_5,
    }

    public enum SpellDeadeyeType {
        annihilate,
        heartstopper,
        hex,
        level_drain,
        soul_steal,
        ricochet,
        cleave,
        knockout,
        double_spell,

        ;
    }
        public enum DeadeyeType {
        decapitate,
        knockout,
        overwhelm,
        heartseeker,
        maim,
        cleave,
        displace,
    }

    public enum HitType {
        critical_miss, //<0
        miss, //0-20
        graze, //20-40
        hit, //40-60
        critical_hit, //60-80
        deadeye, //80+
        ;

        @Override
        public String toString() {
            return StringMaster.format(name());
        }
    }

    public enum BlockType {
        deflect, block,
        ;
    }

    public enum BlockerType {
        shield, weapon, magic,
        ;
    }

    public enum FeatGroup {
        Quick_Item, Token, Spells, Actions, Passives
    }

}
