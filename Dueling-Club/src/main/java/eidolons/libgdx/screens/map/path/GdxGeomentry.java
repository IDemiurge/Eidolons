package eidolons.libgdx.screens.map.path;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JustMe on 3/15/2018.
 */
public class GdxGeomentry {
    public static List<Vector2> getPointsBetween(Vector2 v, Vector2 v2, int n) {
        List<Vector2> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            list.add(new Vector2(v).lerp(v2, 1f / n * (i)));
        }
        return list;
    }
}
