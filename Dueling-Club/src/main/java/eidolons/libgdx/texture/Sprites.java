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


    public static final String HERO_KESERIM = "sprites/unit/keserim2.txt";
    public static final String ROTATING_ARROW = "sprites/ui/parts/blade hand.txt";
    public static final String TORCH = "sprites/ui/light/torch.txt";
    public static final String ALTAR = "sprites/ui/bf/altar.txt";
    public static final String FLOAT_WISP = "sprites/ui/bf/light wisp float.txt";
    public static final String RUNE_INSCRIPTION = "sprites/ui/bf/rune.txt";
    public static final String ORB = "sprites/ui/bf/orb.txt";
    public static final String VEIL = "sprites/ui/veil.txt";
    public static final String HELL_WHEEL = "sprites/ui/parts/underlay.txt";
    public static final String TENTACLE = "sprites/ui/grid/tent loop.txt";
    public static final String WHITE_TENTACLE = "sprites/ui/grid/white tent.txt";
    public static final String PORTAL = "sprites/cells/portal/Portal Loop.txt";
    public static final String PORTAL_OPEN = "sprites/cells/portal/PORTAL OPEN.txt";
    public static final String PORTAL_CLOSE = "sprites/cells/portal/PORTAL CLOSE.txt";
    public static final String INK_BLOTCH = "sprites/ui/misc/INK BLOTCH.txt";
    public static final String COMMENT_KESERIM = "sprites/unit/comment/Keserim comment.txt";

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
