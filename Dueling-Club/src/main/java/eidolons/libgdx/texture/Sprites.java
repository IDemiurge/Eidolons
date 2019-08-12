package eidolons.libgdx.texture;

import main.system.auxiliary.StringMaster;
import sun.reflect.Reflection;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class Sprites {
    public static final String RADIAL = "sprites/ui/radial.txt";
    public static final String BG_DUNGEON = "sprites/ui/backgrounds/dwarf hall.txt";
    public static final String BG_VALLEY = "sprites/ui/backgrounds/valley.txt";
    public static final String BG_BASTION = "sprites/ui/backgrounds/valley.txt";
    public static final String BG_DEFAULT = "sprites/ui/backgrounds/atlas.txt";
    public static final String BG_GATEWAY = BG_DEFAULT; //TODO

    public static final String SHADOW_DEATH = "boss/reaper/attack/sever";
    public static final String SHADOW_SUMMON = "boss/reaper/attack/soul rip";

    public static final String ROTATING_ARROW = "sprites/cells/parts/blade hand.txt";
    public static final String TORCH = "sprites/cells/light/torch.txt";
    public static final String ALTAR = "sprites/cells/bf/altar.txt";
    public static final String FLOAT_WISP = "sprites/cells/bf/light wisp float.txt";
    public static final String RUNE_INSCRIPTION = "sprites/cells/bf/rune.txt";
    public static final String ORB = "sprites/cells/bf/orb.txt";

    public static final String VEIL = "sprites/cells/gate/veil.txt";
    public static final String HELL_WHEEL = "sprites/cells/parts/underlay.txt";
    public static final String TENTACLE = "sprites/cells/grid/tent loop.txt";
    public static final String WHITE_TENTACLE = "sprites/unit/white tent.txt";
    public static final String BONE_WINGS = "sprites/unit/wings.txt";

    public static final String PORTAL = "sprites/cells/portal/portal loop.txt";
    public static final String PORTAL_OPEN = "sprites/cells/portal/portal open.txt";
    public static final String PORTAL_CLOSE = "sprites/cells/portal/portal close.txt";
    public static final String INK_BLOTCH = "sprites/ui/misc/ink blotch.txt";

    public static final String COMMENT_KESERIM = "sprites/hero/comment/keserim comment.txt";
    public static final String HERO_KESERIM = "sprites/hero/keserim2.txt";

    private static final Map<String, String> spriteMap;

    static {
        spriteMap = new HashMap<>();
        for (Field field : Sprites.class.getFields()) {
            try {
                spriteMap.put(StringMaster.getWellFormattedString(field.getName()).toLowerCase(),
                        field.get(null).toString());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

    }

    public static String substituteKey(String key) {
        key = StringMaster.getWellFormattedString(key).toLowerCase();
        if (spriteMap.containsKey(key))
            return spriteMap.get(key);
        return key;
    }

    // "boss/reaper/attack/sever"
}
