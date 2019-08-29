package eidolons.libgdx.gui.panels.headquarters.hero;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import eidolons.libgdx.anims.sprite.SpriteAnimation;
import eidolons.libgdx.anims.sprite.SpriteAnimationFactory;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.gui.panels.headquarters.HqElement;
import eidolons.libgdx.texture.TextureCache;
import main.content.enums.GenericEnums;
import main.swing.generic.components.G_Panel.VISUALS;
import main.system.launch.CoreEngine;

/**
 * Created by JustMe on 4/13/2018.
 */
public class HqHeroViewPanel extends HqElement {

    FadeImageContainer preview;
    Image border;
    HqHeroHeader header;

    SpriteAnimation sprite;

    public HqHeroViewPanel() {
        Group stack;
        add(stack = new Group()).left().bottom();
        left().bottom();

        stack.addActor(preview = new FadeImageContainer());
        stack.addActor(border = new Image(TextureCache.getOrCreateR(VISUALS.FULL_CHARACTER_FRAME.getImgPath())));

//        stack.addActor(header = new HqHeroHeader());
//        preview.setPosition(GdxMaster.centerWidth(header)
//                , (border.getImageHeight() - 700) / 2);
        stack.setSize(border.getImageWidth(), border.getImageHeight());
        setFixedSize(true);
        border.layout();
        setSize(border.getImageWidth(), border.getImageHeight());
        preview.setPosition((border.getImageWidth() - 500) / 2
                , (border.getImageHeight() - 700) / 2);

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (sprite != null) {
            float x = sprite.getX();
            float y = sprite.getY();
            preview.getContent().getDrawable().draw(batch,
                    x, y, 500, 700);

            sprite.draw(batch);
        }
        super.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        preview.act(delta);
        if (sprite != null) {
            sprite.setAlpha(1f);
            //  preview.getContent().getColor().a * 1.5f);
            sprite.centerOnParent(preview);
            sprite.setX(13);
            sprite.setY(sprite.getY() - 50);
        }
    }

    @Override
    protected void update(float delta) {
        preview.setImage(dataSource.getFullPreviewImagePath());
        setSize(border.getImageWidth(), border.getImageHeight());

        if (CoreEngine.isLiteLaunch())
            return;

        sprite = SpriteAnimationFactory.getSpriteAnimation(dataSource.getSpritePath());
        if (sprite == null) {
            return;
        }
        sprite.centerOnParent(this);
        preview.setVisible(false);
        preview.setAlphaTemplate(GenericEnums.ALPHA_TEMPLATE.HQ_HERO_SPRITE);
        sprite.setFrameDuration(0.06f);
    }

}
