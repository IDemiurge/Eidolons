package eidolons.libgdx.gui.panels.dc.unitinfo.neo;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import eidolons.libgdx.TiledNinePatchGenerator;
import eidolons.libgdx.TiledNinePatchGenerator.BACKGROUND_NINE_PATCH;
import eidolons.libgdx.TiledNinePatchGenerator.NINE_PATCH;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.gui.panels.dc.unitinfo.datasource.AvatarDataSource;

public class AvatarPanel extends TablePanelX {
    private final FadeImageContainer portrait;
public static final int h = 404;
    public static final     int w = 274;
    public AvatarPanel() {
        super(w, h);
        //        TextureRegion textureRegion = TextureCache.getOrCreateR("/UI/components/infopanel/avatar-panel.png");
        //        TextureRegionDrawable drawable = new TextureRegionDrawable(textureRegion);
        //        setBackground(NinePatchFactory.getLightDecorPanelFilledDrawable());
        center();
        top();
        portrait = new FadeImageContainer();
        addElement(portrait).pad(22, 60, 22, 55);//.growY();
        setBackground(NinePatchFactory.getLightDecorPanelFilledDrawable());
//        Texture back = TiledNinePatchGenerator.getOrCreateNinePatch(
//         NINE_PATCH.LIGHT, BACKGROUND_NINE_PATCH.PATTERN, w, h);
//        setBackground(new TextureRegionDrawable(new TextureRegion(back)));

        //        row();
        //        nameLabel = new Label("", StyleHolder.getHqLabelStyle(19));
        //        addElement(nameLabel).uniformX().center();
        //        row();
        //
        //        param1Label = new Label("", StyleHolder.getHqLabelStyle(18));
        //        addElement(param1Label).uniformX().center();//.expand();
        //        row();
        //
        //        param2Label = new Label("", StyleHolder.getHqLabelStyle(18));
        //        addElement(param2Label).uniformX().center() ;
        //        pack();
    }

    @Override
    public void updateAct(float delta) {
        AvatarDataSource source = (AvatarDataSource) getUserObject();
        TextureRegion texture = source.getAvatar();
        if (getHeight() >= 350)
            if (getWidth() >= 250)
                texture = source.getLargeImage();

        if (getHeight() >= 700)
            if (getWidth() >= 500)
                texture = source.getFullSizeImage();

        if (texture.getRegionWidth() < 128) {
            texture = source.getAvatar();
        }
        portrait.setImage(new Image(texture));
//        setSize(texture.getRegionWidth(), texture.getRegionHeight());
        //        nameLabel.setText(source.getName());
        //        param1Label.setText(source.getParam1());
        //        param2Label.setText(source.getParam2());
//        pack();
    }
}
