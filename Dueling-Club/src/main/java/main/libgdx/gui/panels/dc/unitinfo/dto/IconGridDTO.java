package main.libgdx.gui.panels.dc.unitinfo.dto;

import com.badlogic.gdx.graphics.Texture;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class IconGridDTO {
    private List<Pair<String, Texture>> icons;

    public IconGridDTO() {
        icons = new ArrayList<>();
    }

    public IconGridDTO add(String name, Texture texture) {
        icons.add(new ImmutablePair<>(name, texture));
        return this;
    }

    public List<Pair<String, Texture>> getIcons() {
        return new ArrayList<>(icons);
    }
}
