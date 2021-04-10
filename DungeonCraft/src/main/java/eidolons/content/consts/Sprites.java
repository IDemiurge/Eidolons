package eidolons.content.consts;

import main.data.filesys.PathFinder;
import main.system.ExceptionMaster;
import main.system.auxiliary.StringMaster;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class Sprites {
    public static final String RADIAL = "sprites/ui/radial.txt";
    public static final String BG_DUNGEON = "sprites/backgrounds/dwarf hall.txt";
    public static final String BG_VALLEY = "sprites/backgrounds/valley.txt";
    public static final String BG_BASTION = "sprites/backgrounds/bastion.txt";
    public static final String BG_DEFAULT = "sprites/backgrounds/atlas.txt";
    public static final String BG_GATEWAY = BG_DEFAULT; //TODO

    public static final String SHADOW_DEATH = "boss/reaper/attack/sever";
    public static final String SHADOW_SUMMON = "boss/reaper/attack/soul rip";

    public static final String ROTATING_ARROW = "sprites/cells/parts/blade hand.txt";
    public static final String TORCH = "sprites/cells/light/torch.txt";
    public static final String ALTAR = "sprites/cells/bf/altar.txt";
    public static final String FLOAT_WISP = "sprites/cells/bf/light wisp float.txt";
    public static final String FIRE_LIGHT = "sprites/cells/bf/fire light.txt";

    public static final String RUNE_INSCRIPTION = "sprites/cells/bf/rune.txt";
    public static final String ORB = "sprites/cells/bf/orb.txt";
    public static final String WATER = "sprites/cells/ambi/black waters.txt";

    public static final String LIGHT_VEIL = "sprites/cells/gate/veil.txt";
    public static final String HELL_WHEEL = "sprites/cells/parts/underlay.txt";
    public static final String TENTACLE = "sprites/cells/grid/tent loop.txt";
    public static final String WHITE_TENTACLE = "sprites/unit/white tent.txt";
    public static final String BONE_WINGS = "sprites/unit/wings.txt";

    public static final String PORTAL = "sprites/cells/portal/portal loop.txt";
    public static final String PORTAL_OPEN = "sprites/cells/portal/portal open.txt";
    public static final String PORTAL_CLOSE = "sprites/cells/portal/portal close.txt";
    public static final String INK_BLOTCH = "sprites/ui/misc/ink blotch.txt";

    public static final String COMMENT_KESERIM = "sprites/unit/comment/keserim comment.txt";
    public static final String HERO_KESERIM = "sprites/hero/keserim2.txt";
    public static final String SNOW = "sprites/particles/snow.txt";
    public static final String MIST = "sprites/particles/mist.txt";

    public static final String AX_FIRE = "sprites/weapons3d/special/ax fire.txt";
    public static final String ACID_BLADE = "sprites/weapons3d/special/acid blade.txt";
    public static final String BIG_CLAW_ATTACK = "sprites/weapons3d/special/big claw slash.txt";
    public static final String BIG_CLAW_IDLE = ("sprites/unit/claw big.txt");
    public static final String SMALL_CLAW_ATTACK = "sprites/weapons3d/special/small claw slash.txt";
    public static final String SMALL_CLAW_IDLE = ("sprites/unit/small claw.txt");

    public static final String BLOOD_SHOWER = "sprites/hit/blood/shower.txt";
    public static final String BLOOD_SLICE = "sprites/hit/blood/slice.txt";
    public static final String BLOOD_SQUIRT = "sprites/hit/blood/squirt.txt";

    public static final String GATE_LIGHTNING = "sprites/spell/nether/lightning gate.txt";
    public static final String KTX_TEST = "sprites/test/maw.txt";
    public static final String GHOST_FIST = "sprites/weapons3d/atlas/screen/ghost/ghost fist.txt";
    public static final String REAPER_SCYTHE = "sprites/weapons3d/atlas/pole arm/scythes/reaper scythe.txt";
    public static final String ARMOR_FIST = "sprites/weapons3d/atlas/natural/fists/armored fist.txt";
    public static final String FANGS = "sprites/weapons3d/atlas/natural/fangs/fangs.txt";
    public static final String CLAWS = "sprites/weapons3d/atlas/natural/claws/claws.txt";
    public static final String KRIS = "sprites/weapons3d/atlas/blade/short swords/kris.txt";
    public static final String BOSS_HARVESTER =  "sprites/boss/reaper/atlas.txt";
    public static final String BOSS_KNIGHT = "sprites/boss/knight/knight.txt";
    public static final String BOSS_KNIGHT_ATTACK = "sprites/boss/knight/main atk.txt";
    public static final String SOULFORCE_BAR_BG_WHITE = "sprites/ui/soulforce bar bg.txt";
    public static final String SOULFORCE_BAR_WHITE = "sprites/ui/soulforce bar.txt";
    public static final String SOULFORCE_BAR_BG = "sprites/ui/soulforce bar bg purple.txt";
    public static final String SOULFORCE_BAR = "sprites/ui/soulforce bar purple.txt";
    public static final String SOULFORCE_CORE = "sprites/ui/soulforce core.txt";
    public static final String THUNDER = "sprites/fly objs/thunder.txt";
    public static final String THUNDER3 = "sprites/fly objs/thunder3.txt";

    private static final Map<String, String> spriteMap;

    static {
        spriteMap = new HashMap<>();
        for (Field field : Sprites.class.getFields()) {
            try {
                spriteMap.put(StringMaster.format(field.getName()).toLowerCase(),
                        field.get(null).toString());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

    }

    public static String substituteKey(String key) {
        key = StringMaster.format(key).toLowerCase();
        if (spriteMap.containsKey(key))
            return spriteMap.get(key);
        return key;
    }

    public static String getHeroSpritePath(String name) {
        return PathFinder.getSpritesPath() + "/hero/" +  StringMaster.cropVersion(name.replace("lvl", "")).trim() + ".txt";
    }


    public static String getByName(String path) {
        try {
            return (String) Sprites.class.getDeclaredField(path.toUpperCase().replace(" ", "_")).get(null);
        }  catch (NoSuchFieldException e) {
            main.system.auxiliary.log.LogMaster.log(1,"No such sprite field: " +path);
        }catch (Exception e) {
            ExceptionMaster.printStackTrace(e);
        }
        return null ;
    }
    // "boss/reaper/attack/sever"
}
