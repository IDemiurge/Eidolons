package main.libgdx.screens.map.layers;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import main.data.filesys.PathFinder;

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
        return PathFinder.getMapLayersPath()+sub.name()+".png";
    }

    public enum ALPHA_MAP{
        ROADS,
        PATHS,
        WILDERNESS,
        WATER,
        OCEAN,
        OBSTACLES,
        ;

    }
    public boolean isThere(int x, int y){
      return  new Color( map.getPixel(x, y)).a==0;
    }
}
