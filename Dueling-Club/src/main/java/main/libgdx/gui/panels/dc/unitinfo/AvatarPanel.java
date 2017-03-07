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
    private final Container<Image> avatarContainer;
    private final Label param2Label;
    private final Label param1Label;
    private final Label nameLabel;

    public AvatarPanel() {
        TextureRegion textureRegion = TextureCache.getOrCreateR("/UI/components/infopanel/avatar-panel.png");
        TextureRegionDrawable drawable = new TextureRegionDrawable(textureRegion);
        background(drawable);
        setWidth(textureRegion.getRegionWidth());
        setHeight(textureRegion.getRegionHeight());
        maxWidth(getWidth());
        maxHeight(getHeight());

        addEmptyCol(60);

        Image avatarImage = new Image();
        avatarContainer = new Container<>(avatarImage);
        avatarContainer.fill().left().bottom();
        avatarContainer.setWidth(128);
        avatarContainer.setHeight(128);
        avatarContainer.setPosition(62, 150);

        addElement(avatarContainer);

        nameLabel = new Label("name", StyleHolder.getDefaultLabelStyle());
        addElement(new Container<>(nameLabel).fill().center().bottom());

        param1Label = new Label("param1", StyleHolder.getDefaultLabelStyle());
        addElement(new Container<>(param1Label).fill().center().bottom());

        param2Label = new Label("param2", StyleHolder.getDefaultLabelStyle());
        addElement(new Container<>(param2Label).fill().center().bottom());
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (updatePanel) {
            AvatarDataSource source = (AvatarDataSource) getUserObject();

            avatarContainer.setActor(new Image(source.getAvatar()));
            nameLabel.setText(source.getName());
            param1Label.setText(source.getParam1());
            param2Label.setText(source.getParam2());

            updatePanel = false;
        }
    }
}
