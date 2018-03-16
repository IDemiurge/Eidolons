package main.libgdx.screens.map.layers;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import main.data.filesys.PathFinder;
import main.libgdx.screens.map.MapScreen;
import main.libgdx.screens.map.path.PixmapPathBuilder;
import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 3/12/2018.
 */
public class AlphaMap {

    private final Pixmap map;
    ALPHA_MAP sub;

    public AlphaMap(ALPHA_MAP sub) {
        this.sub = sub;
        map = new Pixmap(new FileHandle(getPath(sub)));
        if (isWritePoints(sub)){
            PixmapPathBuilder.writePathFile(map,getPointsPath(sub));
        }
    }

    private boolean isWritePoints(ALPHA_MAP sub) {
        switch (sub) {
            case ROADS:
            case PATHS:
                return true;
        }
        return false;
    }

    public static String getPath(ALPHA_MAP sub) {
        return PathFinder.getImagePath() + PathFinder.getMapLayersPath()
         + StringMaster.getWellFormattedString(sub.name()) + ".png";
    }
    public static String getPointsPath(ALPHA_MAP sub) {
        return PathFinder.getImagePath() + PathFinder.getMapLayersPath()
         + StringMaster.getWellFormattedString(sub.name()) + ".txt";
    }

    public boolean isThere(int x, int y) {
        Color c = new Color(map.getPixel(x, MapScreen.defaultSize- y));
        if (c.a == 0)
            return false;
        return true;
    }

    public enum ALPHA_MAP {
        ROADS,
        PATHS,
        WILDERNESS,
        INLAND_WATER,
        OCEAN,
        IMPASSABLE,;

    }
}
