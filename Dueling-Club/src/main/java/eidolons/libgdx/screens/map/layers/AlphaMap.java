package eidolons.libgdx.screens.map.layers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import eidolons.libgdx.GDX;
import eidolons.libgdx.screens.map.MapScreen;
import eidolons.libgdx.screens.map.path.PixmapPathBuilder;
import main.data.filesys.PathFinder;
import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 3/12/2018.
 */
public class AlphaMap {

    private final Pixmap map;
    ALPHA_MAP sub;

    public AlphaMap(ALPHA_MAP sub) {
        this.sub = sub;
        map = new Pixmap(GDX.file(getPath(sub)));
        if (isWritePoints(sub)) {
            PixmapPathBuilder.writePathFile(map, getPointsPath(sub));
        }
    }

    public static String getPath(ALPHA_MAP sub) {
        return PathFinder.getImagePath() + PathFinder.getMapLayersPath()
         + StringMaster.format(sub.name()) + ".png";
    }

    public static String getPointsPath(ALPHA_MAP sub) {
        return PathFinder.getImagePath() + PathFinder.getMapLayersPath()
         + StringMaster.format(sub.name()) + ".txt";
    }

    private boolean isWritePoints(ALPHA_MAP sub) {
        switch (sub) {
            case ROADS:
            case PATHS:
                return false;
        }
        return false;
    }

    public boolean isThere(int x, int y) {
        Color c = new Color(map.getPixel(x, MapScreen.defaultSize - y));
        return !(c.a == 0);
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
