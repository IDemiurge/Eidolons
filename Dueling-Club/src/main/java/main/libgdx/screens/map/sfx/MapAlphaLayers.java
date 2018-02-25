package main.libgdx.screens.map.sfx;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.data.filesys.PathFinder;
import main.game.bf.Coordinates;
import main.libgdx.bf.generic.ImageContainer;
import main.libgdx.texture.TextureCache;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 2/18/2018.
 * <p>
 * read real data from txt?
 * or just encode into filenames?
 * <p>
 * TYPE_X_Y
 */
public class MapAlphaLayers {
    private final Stage stage;
    List<ImageContainer> layers = new ArrayList<>();

    public MapAlphaLayers(Stage stage) {
        this.stage = stage;
        //sync with emitters?
        //point map...
        //desync alpha, randomize, set customly
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
                        String path =
                         StringMaster.removePreviousPathSegments(img.getPath(), PathFinder.getImagePath());
                        Image image = new Image(TextureCache.getOrCreateR(path));
                        ImageContainer container = new ImageContainer(image);
                        container.setFluctuateAlpha(true);
                        layers.add(container);
                        container.setFluctuatingAlpha(RandomWizard.getRandomFloatBetween(type.alphaMin, type.alphaMax));
                        container.setFluctuatingAlphaPauseDuration(type.pauseAtZero);
                        container.setFluctuatingFullAlphaDuration(type.pauseAtFull);
                        container.setAlphaStep(type.alphaStep);
                        container.setFluctuatingAlphaRandomness(type.randomness);
                        if (img.getName().split("_").length < 2)
                            continue;
                        Coordinates c = new Coordinates(true, img.getName().split("_")[1]);
                        container.setPosition(c.x,
                         2988 -
                          c.y);

                    }

        }


        for (ImageContainer sub : layers) {
            stage.addActor(sub);
        }
    }

    private void desyncAlpha() {
        for (ImageContainer sub : layers) {
//
        }
    }

    public enum MAP_LAYER_TYPE {
        //        MIST,
//        CLOUDS,
//        DARKNESS,
        VOLCANO_FIRE(0.1f, 0.3f, 0.1f, 2.5f),
        WISPS(0.15f, 0.9f, 0.5f, 0.5f),
        LIGHTS(0.15f, 0.9f, 0.5f, 0.5f),
//        NIGHT_TIME,
//        SMOKE,
//FULL(0.1f, 0.5f, 0.5f, 0.5f),
//        MIST_SHADOW(0.1f, 0.8f, 0.6f, 0.1f),
        ;
        float alphaStep;
        float alphaMin;
        float alphaMax;
        float pauseAtZero;
        float pauseAtFull;
        float randomness;
        float x, y = 0;

        MAP_LAYER_TYPE() {
        }

        MAP_LAYER_TYPE(float alphaStep, float randomness, float pauseAtZero, float pauseAtFull) {
            this.alphaStep = alphaStep;
            this.pauseAtZero = pauseAtZero;
            this.pauseAtFull = pauseAtFull;
            this.randomness = randomness;
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
