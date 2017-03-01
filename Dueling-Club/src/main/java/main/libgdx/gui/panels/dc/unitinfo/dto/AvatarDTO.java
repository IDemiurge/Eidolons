package main.libgdx.gui.panels.dc.unitinfo.dto;

import com.badlogic.gdx.graphics.Texture;

public class AvatarDTO {
    private Texture image;
    private String name;
    private String param1;
    private String param2;

    public AvatarDTO(Texture image, String name, String param1, String param2) {
        this.image = image;
        this.name = name;
        this.param1 = param1;
        this.param2 = param2;
    }

    public Texture getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getParam1() {
        return param1;
    }

    public String getParam2() {
        return param2;
    }
}
