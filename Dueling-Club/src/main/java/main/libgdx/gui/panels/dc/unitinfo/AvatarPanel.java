package main.libgdx.gui.panels.dc.unitinfo;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import main.libgdx.StyleHolder;
import main.libgdx.gui.panels.dc.TablePanel;
import main.libgdx.texture.TextureCache;

public class AvatarPanel extends TablePanel {
    public AvatarPanel(TextureRegion avatar, String name, String param1, String param2) {
        TextureRegion textureRegion = TextureCache.getOrCreateR("/UI/components/infopanel/avatar-panel.png");
        TextureRegionDrawable drawable = new TextureRegionDrawable(textureRegion);
        background(drawable);
        setWidth(textureRegion.getRegionWidth());
        setHeight(textureRegion.getRegionHeight());
        maxWidth(getWidth());
        maxHeight(getHeight());

        addEmptyCol(60);

        Image avatarImage = new Image(avatar);
        Container<Image> avatarContainer = new Container<>(avatarImage);
        avatarContainer.fill().left().bottom();
        avatarContainer.setWidth(128);
        avatarContainer.setHeight(128);
        avatarContainer.setPosition(62, 150);

        addElement(avatarContainer);

        Label nameLabel = new Label(name, StyleHolder.getDefaultLabelStyle());
        addElement(new Container(nameLabel).fill().center().bottom());

        Label param1Label = new Label(param1, StyleHolder.getDefaultLabelStyle());
        addElement(new Container(param1Label).fill().center().bottom());

        Label param2Label = new Label(param2, StyleHolder.getDefaultLabelStyle());
        addElement(new Container(param2Label).fill().center().bottom());
    }
}
