package main.content.enums.entity;

public class NewRpgEnums {
    public enum WORLD_VIEW {
        faithless,
        blackheart,
        omnivalence,
        tradition,
        zeal;

    }

        public enum ACTIVE_SPACE_VALUE {
        name,
        type,
        mode,
        actives,
        skin,
        index,
        ;

    }
    public enum ACTIVE_SPACE_SKIN{
        lite, //all kinds for grimoires!
    }

    public enum ACTIVE_SPACE_MODE{
        normal, auto, triggered,
    }

    public enum ACTIVE_SPACE_TYPE{
        memorized(2), verbatim(1), divined(5),
        grimoire(3), sphere(4), song(6), custom(10);

            ACTIVE_SPACE_TYPE(int sortIndex) {
                this.sortIndex = sortIndex;
            }

            public int sortIndex;
        }

    public enum PERK_VALUES {
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
        critical_miss,
        miss,
        graze,
        hit,
        critical_hit,
        deadeye,
        ;
    }
}
