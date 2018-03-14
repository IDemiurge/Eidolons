package main.libgdx.screens.map.layers;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import main.data.filesys.PathFinder;
import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 3/12/2018.
 *
 *
 *
 */
public class AlphaMap {

    private final Pixmap map;
    ALPHA_MAP sub;

    public AlphaMap(ALPHA_MAP sub) {
        this.sub = sub;
         map = new Pixmap(new FileHandle(getPath(sub)));
    }

    private String getPath(ALPHA_MAP sub) {
        return PathFinder.getMapLayersPath()+ StringMaster.getWellFormattedString( sub.name())+".png";
    }

    public enum ALPHA_MAP{
        ROADS,
        PATHS,
        WILDERNESS,
        INLAND_WATER,
        OCEAN,
        IMPASSABLE,
        ;

    }
    public boolean isThere(int x, int y){
      return  new Color( map.getPixel(x, y)).a==0;
    }
}
