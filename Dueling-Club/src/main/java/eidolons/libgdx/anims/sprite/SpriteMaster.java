package eidolons.libgdx.anims.sprite;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.bf.boss.anim.BossAnimator;
import eidolons.libgdx.texture.Sprites;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;

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
            list.add(s);
            s.setFps(getFps(over, obj));
            s.setBlending(getBlending(over, obj));
            s.setRotation(getRotation(over, obj, i));
            s.setX(getX(over, obj, i, n));
            s.setY(getY(over, obj, i, n));
//            if (over)
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

    private static float getRotation(boolean over, BattleFieldObject obj, int i) {
        return 360 / n * i;
    }

    private static float getY(boolean over, BattleFieldObject obj, int i, int n) {
        if (over && n <= 1) {
            return 0;
        }
        return -i * s.getHeight() / 12 + i * i * s.getHeight() / 54;
    }

    private static float getX(boolean over, BattleFieldObject obj, int i, int n) {
        if (over && n <= 1) {
            return 0;
        }
        return -i * s.getWidth() / 12 + i * i * s.getWidth() / 54 + s.getWidth() / 2;
    }

    private static String getPath(BattleFieldObject obj, boolean over) {
        String spritePath = null;
        if (over) {
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
                case "Charger":
                    return StringMaster.getStringXTimes(4, Sprites.WHITE_TENTACLE + ";");

                case "Mistborn Horror":
//                    return StringMaster.getStringXTimes(8, Sprites.WHITE_TENTACLE+";");
                    return StringMaster.getStringXTimes(6, Sprites.TENTACLE + ";");
            }

        }
        return spritePath;
    }

    private static SuperActor.BLENDING getBlending(boolean over, BattleFieldObject obj) {
        switch (obj.getName()) {
            case "Dream Siphon":
                return SuperActor.BLENDING.INVERT_SCREEN;
        }
        if (obj.isOverlaying()) {
            return SuperActor.BLENDING.SCREEN;
        }
        if (over) {
            return SuperActor.BLENDING.SCREEN;
        }
        return null;
    }

    private static int getFps(boolean over, BattleFieldObject obj) {
        return DEFAULT_FPS;
    }
}
