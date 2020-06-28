package eidolons.libgdx.bf.decor.shard;

import eidolons.libgdx.GdxImageMaster;
import eidolons.system.options.GraphicsOptions;
import eidolons.system.options.OptionsMaster;
import main.content.enums.GenericEnums;
import main.data.filesys.PathFinder;
import main.game.bf.directions.DIRECTION;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.data.FileManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShardEnums {
    public static GenericEnums.ALPHA_TEMPLATE getTemplateForOverlay(SHARD_OVERLAY overlay) {
        return GenericEnums.ALPHA_TEMPLATE.SHARD_OVERLAY;
    }

    public static GenericEnums.VFX[] getEmitters(SHARD_OVERLAY overlay, SHARD_SIZE size) {
        if (!OptionsMaster.getGraphicsOptions().getBooleanValue(GraphicsOptions.GRAPHIC_OPTION.SHARD_VFX)) {
            return new GenericEnums.VFX[0];
        }
        List<GenericEnums.VFX> list = new ArrayList<>(Arrays.asList(getEmittersForOverlay(overlay)));
        int n = 2;
        if (size != null)
            switch (size) {
                case SMALL:
                    n = 1;
                    break;
                case LARGE:
                    n = 3;
                    break;
            }
        n = RandomWizard.getRandomInt(n);
        if (n < 0) {
            return new GenericEnums.VFX[0];
        }
        GenericEnums.VFX[] array = new GenericEnums.VFX[n];
        for (int i = 0; i < n; i++) {
            array[i] = list.remove(
                    RandomWizard.getRandomIndex(list));
        }
        return array;
    }

    private static GenericEnums.VFX[] getEmittersForOverlay(SHARD_OVERLAY overlay) {
        if (overlay == null) {
            overlay = new EnumMaster<SHARD_OVERLAY>().
                    getRandomEnumConst(SHARD_OVERLAY.class);
        }
        switch (overlay) {
            case MIST:
                return new GenericEnums.VFX[]{
                        GenericEnums.VFX.MIST_ARCANE,
                        GenericEnums.VFX.DARK_MIST_LITE,
                        GenericEnums.VFX.THUNDER_CLOUDS_CRACKS,
                        GenericEnums.VFX.THUNDER_CLOUDS_CRACKS,
                        //                 EMITTER_PRESET.MIST_TRUE2,
                        GenericEnums.VFX.MIST_WHITE,
                        GenericEnums.VFX.MIST_WIND,
                        GenericEnums.VFX.MIST_BLACK,
                        GenericEnums.VFX.MIST_WIND,
                        GenericEnums.VFX.STARS,
                        GenericEnums.VFX.ASH,
                        GenericEnums.VFX.MOTHS_TIGHT2,
                        GenericEnums.VFX.WISPS,
                        GenericEnums.VFX.SNOW_TIGHT2,
                        GenericEnums.VFX.SNOW,
                        GenericEnums.VFX.MOTHS_TIGHT2,
                        GenericEnums.VFX.STARS
                };
            case DARKNESS:
                return new GenericEnums.VFX[]{
                        GenericEnums.VFX.DARK_MIST,
                        GenericEnums.VFX.MIST_ARCANE,
                        GenericEnums.VFX.MIST_ARCANE,
                        GenericEnums.VFX.MIST_ARCANE,
                        GenericEnums.VFX.DARK_MIST_LITE,
                        GenericEnums.VFX.MIST_BLACK,
                        GenericEnums.VFX.CINDERS3,
                        GenericEnums.VFX.ASH,
                        GenericEnums.VFX.MIST_WHITE,
                        GenericEnums.VFX.MIST_BLACK,
                        GenericEnums.VFX.MIST_WIND,
                        GenericEnums.VFX.STARS,
                        GenericEnums.VFX.ASH,
                        GenericEnums.VFX.MOTHS_TIGHT2,
                        GenericEnums.VFX.WISPS,
                        GenericEnums.VFX.SNOW_TIGHT2,
                        GenericEnums.VFX.SNOW,
                        GenericEnums.VFX.MOTHS_TIGHT2,
                        GenericEnums.VFX.STARS
                };
            case NETHER:
                return new GenericEnums.VFX[]{
                        GenericEnums.VFX.DARK_MIST,
                        GenericEnums.VFX.DARK_MIST_LITE,
                        GenericEnums.VFX.MIST_ARCANE,
                        GenericEnums.VFX.MIST_ARCANE,
                        GenericEnums.VFX.MIST_ARCANE,
                        GenericEnums.VFX.MIST_BLACK,
                        GenericEnums.VFX.CINDERS3,
                        GenericEnums.VFX.WISPS,
                        GenericEnums.VFX.STARS,
                        GenericEnums.VFX.MIST_BLACK,
                        GenericEnums.VFX.MIST_BLACK,
                        GenericEnums.VFX.MIST_WHITE3,
                        GenericEnums.VFX.MIST_WIND,
                        GenericEnums.VFX.THUNDER_CLOUDS_CRACKS,
                        GenericEnums.VFX.MIST_WIND
                };
        }
        return new GenericEnums.VFX[0];
    }

    public static String getBackgroundTexturePath(Object arg, DIRECTION direction, SHARD_TYPE type, SHARD_SIZE size) {
        String path = null;
        String name=null ;
        if (direction == null) {
            path = StrPathBuilder.build("ui", "cells", "shards",
                    type.toString(), "isles"  );
            name="isle";
        } else if (direction.isDiagonal()) {
            path = StrPathBuilder.build("ui", "cells", "shards",
                    type.toString(), arg.toString());
        } else {
            if (size == null) {
                return null;
            }
            //TODO naming hack - so cheap...
            path = StrPathBuilder.build("ui", "cells", "shards",
                    type.toString(), arg.toString());

            name = size == SHARD_SIZE.SMALL
                    ? size + "up"
                    : size + "down";

        }
        if (name == null) {
            return null;
        }
        String file = FileManager.getRandomFilePathVariantSmart(
                name, PathFinder.getImagePath() +
                        path, ".png");
        if (file == null) {
            return null;
        }
        return GdxImageMaster.cropImagePath(file);
    }

    public static String getForegroundTexturePath(Object arg, SHARD_OVERLAY overlay) {
        return StrPathBuilder.build("ui", "cells", "shards", "overlay",
                overlay.toString(),
                //         size.toString() +
                arg + ".png");
    }

    public static String getRandomForegroundTexturePath(Object arg, SHARD_OVERLAY overlay) {
        String path = getForegroundTexturePath(arg, overlay);

        String file = FileManager.getRandomFilePathVariant(
                PathFinder.getImagePath() +
                        path, ".png", false, false);
        if (file == null) {
            return null;
        }
        return GdxImageMaster.cropImagePath(file);
    }

    public enum SHARD_OVERLAY {
        MIST,
        DARKNESS,
        NETHER,

    }

    public enum SHARD_SIZE {
        SMALL,
        NORMAL {
            @Override
            public String toString() {
                return "";
            }
        },
        LARGE,
        ;

        public String toString() {
            return name() + " ";
        }
    }

    public enum SHARD_TYPE {
        ROCKS,
        ROOTS,
        CHAINS,

    }
}
