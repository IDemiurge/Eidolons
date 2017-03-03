package main.libgdx.gui.panels.dc.unitinfo.dto;

import com.badlogic.gdx.graphics.Texture;

public class ValueDTO {
    private Texture icon;
    private String name;
    private String text;

    public ValueDTO(Texture icon, String name, String text) {
        this.icon = icon;
        this.name = name;
        this.text = text;
    }

    public ValueDTO(String name, String text) {
        this.name = name;
        this.text = text;
    }

    public ValueDTO(Texture icon, String text) {
        this.icon = icon;
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public Texture getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }
}
