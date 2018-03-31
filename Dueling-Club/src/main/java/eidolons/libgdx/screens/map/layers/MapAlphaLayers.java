package eidolons.libgdx.screens.map.layers;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import eidolons.libgdx.screens.map.MapScreen;
import main.content.enums.macro.MACRO_CONTENT_CONSTS.DAY_TIME;
import main.data.filesys.PathFinder;
import main.game.bf.Coordinates;
import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.bf.SuperActor.ALPHA_TEMPLATE;
import eidolons.libgdx.bf.generic.ImageContainer;
import eidolons.libgdx.texture.TextureCache;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.auxiliary.data.MapMaster;

import java.io.File;

import static main.content.enums.macro.MACRO_CONTENT_CONSTS.DAY_TIME.*;

/**
 * Created by JustMe on 2/18/2018.
 * <p>
 * read real data from txt?
 * or just encode into filenames?
 * <p>
 * TYPE_X_Y
 */
public class MapAlphaLayers extends MapTimedLayer<ImageContainer> {

    public MapAlphaLayers() {
        super();
    }

    public static String getMainPath() {
        return PathFinder.getImagePath() + StrPathBuilder.build("global", "map", "layers", "alpha");
    }


    public void init() {
        for (File subdir : FileManager.getFilesFromDirectory(getMainPath(), true)) {
            MAP_LAYER_TYPE type = new EnumMaster<MAP_LAYER_TYPE>().
             retrieveEnumConst(MAP_LAYER_TYPE.class, subdir.getName().toUpperCase());
            if (type != null)
                if (subdir.isDirectory())
                    for (File img : subdir.listFiles()
//            FileManager.getFilesFromDirectory(subdir.getPath(), true)
                     ) {
                        if (!FileManager.isImageFile(img.getName()))
                            continue;
                        String path = img.getPath();
                        try {
                            path = StringMaster.removePreviousPathSegments(img.getPath(), PathFinder.getImagePath());
                        } catch (Exception e) {
                            main.system.ExceptionMaster.printStackTrace(e);
                        }
                        Image image = new Image(TextureCache.getOrCreateR(path));
                        ImageContainer container = new ImageContainer(image);
                        container.setFluctuateAlpha(true);
                        container.setFluctuatingAlpha(RandomWizard.getRandomFloatBetween(type.alphaMin, type.alphaMax));
                        container.setFluctuatingAlphaPauseDuration(type.pauseAtZero);
                        container.setFluctuatingFullAlphaDuration(type.pauseAtFull);
                        container.setFluctuatingAlphaMin(type.alphaMin);
                        container.setFluctuatingAlphaMax(type.alphaMax);
                        container.setAlphaStep(type.alphaStep);
                        container.setFluctuatingAlphaRandomness(type.randomness);
                        if (img.getName().split("_").length < 2)
                            continue;
                        Coordinates c = new Coordinates(true, img.getName().split("_")[1]);
                        container.setPosition(c.x,
                         MapScreen.defaultSize -
                          c.y);
                        for (DAY_TIME sub : type.times)
                            MapMaster.addToListMap(map, sub, container);
                    }

        }
        initialized = true;

    }


    public enum MAP_LAYER_TYPE {
        //        MIST,
//        CLOUDS,
//        DARKNESS,
        MAGIC(0.3f, 0.5f, 0.5f, 1f, 0.1f, 2.5f, NIGHTFALL, MIDNIGHT, DAWN),
        COMMONS(0.1f, 0.3f, 0.1f, 2.5f),
        VOLCANO_FIRE(0.1f, 0.3f, 0.1f, 2.5f),
        WISPS(0.15f, 2.9f, 0.5f, 0.5f, NIGHTFALL, MIDNIGHT, DAWN),
        LIGHTS(0.15f, 0.9f, 0.5f, 1f, 0.15f, 2.5f, NIGHTFALL, MIDNIGHT, DAWN),
        SUNLIGHT(0.15f, 0.9f, 0.5f, 0.5f, MORNING, MIDDAY),
//        NIGHT_TIME,
//        SMOKE,
//FULL(0.1f, 0.5f, 0.5f, 0.5f),
//        MIST_SHADOW(0.1f, 0.8f, 0.6f, 0.1f),
        ;
        public DAY_TIME[] times;//TODO maybe different alpha?!
        ALPHA_TEMPLATE template;
        float alphaStep;
        float alphaMin;
        float alphaMax = 1;
        float pauseAtZero;
        float pauseAtFull;
        float randomness;
        float x, y = 0;

        MAP_LAYER_TYPE() {
        }

        MAP_LAYER_TYPE(float alphaStep, float randomness,
                       float alphaMin, float alphaMax,
                       float pauseAtZero, float pauseAtFull
         , DAY_TIME... times) {
            this.alphaMin = alphaMin;
            this.alphaMax = alphaMax;
            this.times = times;
            if (times.length == 0)
                this.times = DAY_TIME.values;
            this.alphaStep = alphaStep;
            this.pauseAtZero = pauseAtZero;
            this.pauseAtFull = pauseAtFull;
            this.randomness = randomness;
        }

        MAP_LAYER_TYPE(float alphaStep, float randomness, float pauseAtZero, float pauseAtFull
         , DAY_TIME... times) {
            this(alphaStep, randomness, SuperActor.DEFAULT_ALPHA_MIN, SuperActor.DEFAULT_ALPHA_MAX,
             pauseAtZero, pauseAtFull, times);
        }

        MAP_LAYER_TYPE(float alphaStep, float alphaMin, float alphaMax, float pauseAtZero, float randomness) {
            this.alphaStep = alphaStep;
            this.alphaMin = alphaMin;
            this.alphaMax = alphaMax;
            this.pauseAtZero = pauseAtZero;
            this.randomness = randomness;
        }
        //IDEA: link layers in alpha sync or conter-phase

        //movement
        //time_of_day
        //colorize
        //rotate
        //Point p;

    }
}
