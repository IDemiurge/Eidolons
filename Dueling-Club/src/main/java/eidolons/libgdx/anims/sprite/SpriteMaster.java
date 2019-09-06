package eidolons.libgdx.anims.sprite;

import eidolons.content.PROPS;
import eidolons.entity.obj.BattleFieldObject;
import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.bf.boss.anim.BossAnimator;
import eidolons.libgdx.texture.Sprites;
import main.content.enums.entity.BfObjEnums;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;

import java.util.ArrayList;
import java.util.List;

public class SpriteMaster {
    private static final int DEFAULT_FPS = 12;
    private static int n = 1;
    private static SpriteX s;

    public static List<SpriteX> getSpriteForUnit(BattleFieldObject obj, boolean over) {
        String path = getPath(obj, over);
        List<SpriteX> list = new ArrayList<>();
        if (path == null) {
            return list;
        }
        List<String> paths = ContainerUtils.openContainer(path);
        n = paths.size();
        for (int i = 0; i < paths.size(); i++) {
            s = new SpriteX(paths.get(i));
            BfObjEnums.SPRITES sprite = null;
            for (BfObjEnums.SPRITES sprites : BfObjEnums.SPRITES.values()) {
                if (sprite== BfObjEnums.SPRITES.EMPTY) {
                    continue;
                }
                if (FileManager.formatPath(sprites.path, true).equalsIgnoreCase(
                        FileManager.formatPath(paths.get(i), true))) {
                            sprite = sprites;
                }
            }
            if (sprite == null) {
                sprite = BfObjEnums.SPRITES.EMPTY;
            }
            list.add(s);
            s.setFlipX(isFlipX(sprite, n, i));
//            s.setFlipyX(isFlipX( paths.getVar(i), n , i ));
            s.setFps(getFps(sprite, over, obj));
            s.setBlending(getBlending(sprite, over, obj));
            s.setRotation(getRotation(sprite, over, obj, i));
            s.setX(getX(sprite, over, obj, i, n));
            s.setY(getY(sprite, over, obj, i, n));
//            if (over)
            boolean offset = isOffset(sprite, over, i, n);
            if (offset)
            {
                s.getSprite().setOffsetX(s.getWidth() / 2 - 64);
                s.getSprite().setOffsetY(s.getHeight() / 2 - 64);
            }
            s.setOrigin(0, s.getHeight() / 2);
            if (obj.isOverlaying()) {
                s.setScale(0.5f);
            }
            s.act(RandomWizard.getRandomFloatBetween(0, 3));
        }
//        s.setScale();
//        s.setColor();

        return list;
    }

    private static boolean isOffset(BfObjEnums.SPRITES sprite, boolean over, int i, int n) {
        switch (sprite) {
            case BONE_WINGS:
                return false;
        }
        return true;
    }

    private static boolean isFlipX(BfObjEnums.SPRITES s, int n, int i) {
        boolean odd = i % 2 == 1;
        switch (s) {
            case BONE_WINGS:
                return !odd;
        }
        return false;
    }

    private static float getRotation(BfObjEnums.SPRITES sprite, boolean over, BattleFieldObject obj, int i) {
        switch (sprite) {
            case TENTACLE:
            case WHITE_TENTACLE:
                if (obj.getName().contains("Adept")) {
                    return 90;
                }
                return 360 / n * i;
        }
        return 0;
    }

    private static float getY(BfObjEnums.SPRITES sprite, boolean over, BattleFieldObject obj, int i, int n) {
        if (over && n <= 1) {
            return 0;
        }
        return -i * s.getHeight() / 12 + i * i * s.getHeight() / 54;
    }

    private static float getX(BfObjEnums.SPRITES sprite, boolean over, BattleFieldObject obj, int i, int n) {
        if (over && n <= 1) {
            return 0;
        }
        return -i * s.getWidth() / 12 + i * i * s.getWidth() / 54 + s.getWidth() / 2;
    }

    private static String getPath(BattleFieldObject obj, boolean over) {
        String spritePath = null;
        String parsed = "";
        String toParse = "";
        if (over) {
            toParse = obj.getProperty(PROPS.OVERLAY_SPRITES);
        } else if (!obj.getProperty(PROPS.UNDERLAY_SPRITES).isEmpty()) {
            toParse = obj.getProperty(PROPS.UNDERLAY_SPRITES);
        }
        if (!toParse.isEmpty()) {
            for (String s : ContainerUtils.openContainer(toParse)) {
                BfObjEnums.SPRITES c = new EnumMaster<BfObjEnums.SPRITES>().
                        retrieveEnumConst(BfObjEnums.SPRITES.class, s);
                if (c != null) {
                    parsed += c.path + ";";
                }
            }
            return parsed;
        }
        if (over) {
            if (obj.isLightEmitter() ) {
                return Sprites.FIRE_LIGHT;
            }
            if (obj.isBoss()) {
                spritePath = BossAnimator.getSpritePath(obj);
            } else {
                switch (obj.getName()) {
                    case "Dream Siphon":
                        return StringMaster.getStringXTimes(4, Sprites.WHITE_TENTACLE + ";");
                    case "Mystic Pool":
                        return StringMaster.getStringXTimes(2, Sprites.FLOAT_WISP + ";");
                    case ("Ghost Light"):
                        return Sprites.FLOAT_WISP;
                    case ("Inscription"):
                        return Sprites.RUNE_INSCRIPTION;
                    case ("Torch"):
                        return Sprites.TORCH;
                    case ("Altar"):
                        return Sprites.ALTAR;
                    case ("Eldritch Sphere"):
                        return Sprites.ORB;
                }
            }
        } else {
//            if (obj instanceof Unit)

            switch (obj.getName()) {
//                case "Hollow Adept":
//                    return Sprites.BONE_WINGS;
//                      return StringMaster.getStringXTimes(2, Sprites.BONE_WINGS + ";");
//                    return StringMaster.getStringXTimes(2, Sprites.WHITE_TENTACLE + ";");

                case "Charger":
                    return StringMaster.getStringXTimes(4, Sprites.WHITE_TENTACLE + ";");
                case "Pale Wing":
                case "Black Wing":
                    return StringMaster.getStringXTimes(2, Sprites.BONE_WINGS + ";");
                case "Mistborn Horror":
//                    return StringMaster.getStringXTimes(8, Sprites.WHITE_TENTACLE+";");
                    return StringMaster.getStringXTimes(6, Sprites.WHITE_TENTACLE + ";");
            }

        }
        return spritePath;
    }

    private static SuperActor.BLENDING getBlending(BfObjEnums.SPRITES sprite, boolean over, BattleFieldObject obj) {
        switch (obj.getName()) {
            case "Adeptus Carnifex":
            case "Netherbound Horror":
                return SuperActor.BLENDING.INVERT_SCREEN;
            case "Hollow Adept":
                return SuperActor.BLENDING.SCREEN;
            case "Black Wing":
            case "Mistborn Horror":
            case "Dream Siphon":
                return SuperActor.BLENDING.INVERT_SCREEN;
        }
        switch (sprite) {
            case VEIL:
            case FLOAT_WISP:
            case FIRE_LIGHT:
            case WHITE_TENTACLE:
                return SuperActor.BLENDING.SCREEN;
        }
        if (obj.isOverlaying()) {
            return SuperActor.BLENDING.SCREEN;
        }
        if (over) {
            return SuperActor.BLENDING.SCREEN;
        }
        return null;
    }

    private static int getFps(BfObjEnums.SPRITES sprite, boolean over, BattleFieldObject obj) {
        return DEFAULT_FPS;
    }
}
