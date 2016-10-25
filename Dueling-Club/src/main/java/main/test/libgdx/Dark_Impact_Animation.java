package main.test.libgdx;

import com.badlogic.gdx.graphics.Texture;
import main.data.filesys.PathFinder;

import java.util.ArrayList;

/**
 * Created by PC on 22.10.2016.
 */
public class Dark_Impact_Animation {
    private static ArrayList<Texture> list_of_dark_impacts;


    public Dark_Impact_Animation() {
        String q = PathFinder.getImagePath();
        list_of_dark_impacts = new ArrayList<>();
        for (int i = 1; i < 9; i++) {
            PathFinder.init();
            String path = PathFinder.getImagePath();
//            Texture local_Texture = new Texture("D:\\MyRepository\\eidolons-battlecraft\\Dueling-Club\\target\\dependency\\img\\mini\\sprites\\impact\\dark impact\\dark impact" + i + ".jpg");
            Texture local_Texture = new Texture(path + "mini\\sprites\\impact\\dark impact\\dark impact" + i + ".jpg");
            local_Texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            list_of_dark_impacts.add(local_Texture);
        }
    }

    public Texture getTexture(float current_timing) {
        Texture local = null;
        if (current_timing >= 0 && current_timing <= 0.05) {
            local = list_of_dark_impacts.get(0);

        }
        if (current_timing > 0.05 && current_timing <= 0.1) {
            local = list_of_dark_impacts.get(1);

        }
        if (current_timing > 0.1 && current_timing <= 0.15) {
            local = list_of_dark_impacts.get(2);
        }
        if (current_timing > 0.15 && current_timing <= 0.2) {
            local = list_of_dark_impacts.get(3);

        }
        if (current_timing > 0.2 && current_timing <= 0.25) {
            local = list_of_dark_impacts.get(4);

        }
        if (current_timing > 0.25 && current_timing <= 0.3) {
            local = list_of_dark_impacts.get(5);
        }
        if (current_timing > 0.3 && current_timing <= 0.35) {
            local = list_of_dark_impacts.get(6);
        }
        if (current_timing > 0.35 ) {
            local = list_of_dark_impacts.get(7);

        }
        return local;
    }
}

