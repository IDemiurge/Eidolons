package main.libgdx.gui.panels.dc.unitinfo.dto;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Container;

public class WeaponDetailInfoDTO {
    private Texture icon;
    private Container tooltip;

    public WeaponDetailInfoDTO(Texture icon, Container tooltip) {
        this.icon = icon;
        this.tooltip = tooltip;
    }

    public Texture getIcon() {
        return icon;
    }

    public Container getTooltip() {
        return tooltip;
    }
}
