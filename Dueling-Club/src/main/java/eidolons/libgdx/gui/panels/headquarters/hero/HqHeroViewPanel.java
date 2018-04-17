package eidolons.libgdx.gui.panels.headquarters.hero;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
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
        Stack stack;
        add(stack = new Stack());
        stack.add(preview = new FadeImageContainer());
        stack.add(border = new Image(TextureCache.getOrCreateR(VISUALS.FULL_CHARACTER_FRAME.getImgPath())));
        stack.setSize(border.getImageWidth(), border.getImageHeight());
    }

    @Override
    protected void update(float delta) {
        preview.setImage(dataSource.getFullPreviewImagePath());
    }
    //emblem?

}
