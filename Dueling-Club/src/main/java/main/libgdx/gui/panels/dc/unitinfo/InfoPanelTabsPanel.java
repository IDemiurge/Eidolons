package main.libgdx.gui.panels.dc.unitinfo;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import main.libgdx.gui.panels.dc.TabbedPanel;
import main.libgdx.texture.TextureCache;

public class InfoPanelTabsPanel extends TabbedPanel {

    @Override
    protected TextButton.TextButtonStyle getButtonStyle() {
        Texture buttonTexture = TextureCache.getOrCreate("/UI/components/infopanel/buttons.png");
        TextureRegion pressed = new TextureRegion(buttonTexture, 0, 0, 59, 28);
        TextureRegion released = new TextureRegion(buttonTexture, 60, 0, 59, 28);
        TextButton.TextButtonStyle style = super.getButtonStyle();
        style.checked = style.down = new TextureRegionDrawable(pressed);
        style.up = new TextureRegionDrawable(released);
        return style;
    }
}
