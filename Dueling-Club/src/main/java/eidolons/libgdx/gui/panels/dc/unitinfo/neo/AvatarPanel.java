package eidolons.libgdx.gui.panels.dc.unitinfo.neo;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.gui.panels.dc.unitinfo.datasource.AvatarDataSource;

public class AvatarPanel extends TablePanelX {
    private final Cell<Image> avatarContainer;
    private final Label param2Label;
    private final Label param1Label;
    private final Label nameLabel;

    public AvatarPanel() {
//        TextureRegion textureRegion = TextureCache.getOrCreateR("/UI/components/infopanel/avatar-panel.png");
//        TextureRegionDrawable drawable = new TextureRegionDrawable(textureRegion);
//        setBackground(drawable);
        setSize(150, 200);
        Image avatarImage = new Image();
        avatarContainer = addElement(avatarImage).pad(22, 60, 22, 55);
        row();
        nameLabel = new Label("name", StyleHolder.getHqLabelStyle(16));
        nameLabel.setAlignment(Align.center);
        addElement(nameLabel).expand();
        row();
        param1Label = new Label("param1", StyleHolder.getHqLabelStyle(16));
        param1Label.setAlignment(Align.center);
        addElement(param1Label).expand();
        row();
        param2Label = new Label("param2", StyleHolder.getHqLabelStyle(16));
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
