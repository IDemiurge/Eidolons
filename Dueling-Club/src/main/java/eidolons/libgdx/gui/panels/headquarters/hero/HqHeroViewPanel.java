package eidolons.libgdx.gui.panels.headquarters.hero;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.gui.panels.headquarters.HqElement;
import eidolons.libgdx.texture.TextureCache;
import main.swing.generic.components.G_Panel.VISUALS;

/**
 * Created by JustMe on 4/13/2018.
 */
public class HqHeroViewPanel extends HqElement {

    FadeImageContainer preview;
    Image border;

    public HqHeroViewPanel() {
        Group stack;
        add(stack = new Group()).left().bottom();
        left().bottom();
        stack.addActor(preview = new FadeImageContainer());
        stack.addActor(border = new Image(TextureCache.getOrCreateR(VISUALS.FULL_CHARACTER_FRAME.getImgPath())));

        stack.setSize(border.getImageWidth(), border.getImageHeight());
        setFixedSize(true);
        border.layout();
        setSize(border.getImageWidth(), border.getImageHeight());
        preview.setPosition((border.getImageWidth() - 500) / 2
         , (border.getImageHeight() - 700) / 2);

    }

    @Override
    protected void update(float delta) {
        preview.setImage(dataSource.getFullPreviewImagePath());

    }
    //emblem?

}
