package main.libgdx.screens.map.sfx;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.data.filesys.PathFinder;
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
 *
 * read real data from txt?
 * or just encode into filenames?
 *
 * TYPE_X_Y
 *
 */
public class MapAlphaLayers {
    private final Stage stage;

    public enum MAP_LAYER_TYPE{
//        MIST,
//        CLOUDS,
//        DARKNESS,
//        VOLCANO_FIRE,
//        NIGHT_TIME,
//        SMOKE,
        FULL(0.1f, 0.5f, 0.5f),
    ;
        float alphaStep;
        float alphaMin;
        float alphaMax;
        float pauses;
        float randomness;
        float x, y=0;

        MAP_LAYER_TYPE() {
        }

        MAP_LAYER_TYPE(float alphaStep, float pauses, float randomness) {
            this.alphaStep = alphaStep;
            this.pauses = pauses;
            this.randomness = randomness;
        }

        MAP_LAYER_TYPE(float alphaStep, float alphaMin, float alphaMax, float pauses, float randomness) {
            this.alphaStep = alphaStep;
            this.alphaMin = alphaMin;
            this.alphaMax = alphaMax;
            this.pauses = pauses;
            this.randomness = randomness;
        }
        //IDEA: link layers in alpha sync or conter-phase

        //movement
        //time_of_day
        //colorize
        //rotate
        //Point p;

    }

    List<ImageContainer> layers = new ArrayList<>();

    public MapAlphaLayers(Stage stage) {
        this.stage=stage;
        //sync with emitters?
        //point map...
        //desync alpha, randomize, set customly
    }

    public void init(){

        for (File subdir:     FileManager.getFilesFromDirectory(getMainPath(), true)){
            MAP_LAYER_TYPE type=new EnumMaster<MAP_LAYER_TYPE>().
             retrieveEnumConst(MAP_LAYER_TYPE.class, subdir.getName().toUpperCase());
            for (File img:subdir.listFiles()
//            FileManager.getFilesFromDirectory(subdir.getPath(), true)
            ){
                if (!FileManager.isImageFile(img.getName()))
                    continue;
                String path =
                 StringMaster.removePreviousPathSegments(img.getPath(), PathFinder.getImagePath());
                Image image = new Image(TextureCache.getOrCreateR(path));
                ImageContainer container=new ImageContainer(image);
                container.setFluctuateAlpha(true);
                layers.add( container);
                container.setFluctuatingAlpha(RandomWizard.getRandomFloatBetween(type.alphaMin, type.alphaMax));
                container.setFluctuatingAlphaPauseDuration(type.pauses);
                container.setFluctuatingFullAlphaDuration(type.pauses);
                container.setAlphaStep(type.alphaStep);
                container.setFluctuatingAlphaRandomness(type.randomness);

//                container.setPosition( x,  y); TODO
            }

            }


        for (ImageContainer sub : layers) {
        stage.addActor(sub);
        }
    }

    public static String getMainPath() {
        return PathFinder.getImagePath()+StrPathBuilder.build("global", "map", "layers", "alpha");
    }

    private void desyncAlpha() {
        for (ImageContainer sub : layers) {
//
        }
    }
}
