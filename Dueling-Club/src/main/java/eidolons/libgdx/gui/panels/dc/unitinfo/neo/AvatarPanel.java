package eidolons.libgdx.gui.panels.dc.unitinfo.neo;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.gui.panels.dc.unitinfo.datasource.AvatarDataSource;
import eidolons.libgdx.texture.TextureCache;

public class AvatarPanel extends TablePanel {
    private final Cell<Image> avatarContainer;
    private final Label param2Label;
    private final Label param1Label;
    private final Label nameLabel;

    public AvatarPanel() {
        TextureRegion textureRegion = TextureCache.getOrCreateR("/UI/components/infopanel/avatar-panel.png");
        TextureRegionDrawable drawable = new TextureRegionDrawable(textureRegion);
        setBackground(drawable);

        Image avatarImage = new Image();
        avatarContainer = addElement(avatarImage).pad(22, 60, 22, 55);
        row();
        nameLabel = new Label("name", StyleHolder.getDefaultLabelStyle());
        nameLabel.setAlignment(Align.center);
        addElement(nameLabel).expand();
        row();
        param1Label = new Label("param1", StyleHolder.getDefaultLabelStyle());
        param1Label.setAlignment(Align.center);
        addElement(param1Label).expand();
        row();
        param2Label = new Label("param2", StyleHolder.getDefaultLabelStyle());
        param1Label.setAlignment(Align.center);
        addElement(param2Label).expand();
    }

    @Override
    public void updateAct(float delta) {
        AvatarDataSource source = (AvatarDataSource) getUserObject();

        avatarContainer.setActor(new Image(source.getAvatar()));
        nameLabel.setText(source.getName());
        param1Label.setText(source.getParam1());
        param2Label.setText(source.getParam2());
    }
}
