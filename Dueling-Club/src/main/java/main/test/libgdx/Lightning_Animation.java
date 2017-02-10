package main.test.libgdx;

import com.badlogic.gdx.graphics.Texture;
import main.data.filesys.PathFinder;

import java.util.ArrayList;

/**
 * Created by PC on 22.10.2016.
 */
public class Lightning_Animation {
        private static ArrayList<Texture> list_of_lightnings;

    public Lightning_Animation(){
        String path = PathFinder.getImagePath();

        list_of_lightnings = new ArrayList<>();
        for (int i = 1;i<18;i++){
            Texture local_Texture = new Texture(path + "mini\\sprites\\impact\\electro impact\\e"+i+".jpg");
            local_Texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            list_of_lightnings.add(local_Texture);
        }
    }
    public Texture getTexture(float current_timing) {
        Texture local = null;
        if (current_timing >= 0 && current_timing <= 0.02) {
            local = list_of_lightnings.get(0);

        }
        if (current_timing > 0.02 && current_timing <= 0.04) {
            local = list_of_lightnings.get(1);

        }
        if (current_timing > 0.04 && current_timing <= 0.06) {
            local = list_of_lightnings.get(2);
        }
        if (current_timing > 0.06 && current_timing <= 0.08) {
            local = list_of_lightnings.get(3);

        }
        if (current_timing > 0.08 && current_timing <= 0.1) {
            local = list_of_lightnings.get(4);

        }
        if (current_timing > 0.1 && current_timing <= 0.12) {
            local = list_of_lightnings.get(5);
        }
        if (current_timing > 0.12 && current_timing <= 0.14) {
            local = list_of_lightnings.get(6);
        }
        if (current_timing > 0.14 && current_timing <= 0.16) {
            local = list_of_lightnings.get(7);
        }
        if (current_timing > 0.16 && current_timing <= 0.18) {
            local = list_of_lightnings.get(8);
        }
        if (current_timing > 0.18 && current_timing <= 0.2) {
            local = list_of_lightnings.get(9);
        }
        if (current_timing > 0.2 && current_timing <= 0.22) {
            local = list_of_lightnings.get(10);
        }
        if (current_timing > 0.22 && current_timing <= 0.24) {
            local = list_of_lightnings.get(11);
        }
        if (current_timing > 0.24 && current_timing <= 0.26) {
            local = list_of_lightnings.get(12);
        }
        if (current_timing > 0.26 && current_timing <= 0.28) {
            local = list_of_lightnings.get(13);
        }
        if (current_timing > 0.28 && current_timing <= 0.3) {
            local = list_of_lightnings.get(14);
        }
        if (current_timing > 0.3 && current_timing <= 0.32) {
            local = list_of_lightnings.get(15);
        }
        if (current_timing > 0.32 && current_timing <= 0.34) {
            local = list_of_lightnings.get(16);
        }
//        if (current_timing > 0.34) {
//            time_Counter = 0;
//        }

        return local;
    }

}
