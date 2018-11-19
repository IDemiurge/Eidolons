package eidolons.game.battlecraft.logic.meta.scenario.dialogue.view;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.LabelX;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.texture.TextureCache;
import main.system.graphics.FontMaster.FONT;

/**
 * Created by JustMe on 11/16/2018.
 */
public class MessageActor extends TablePanelX {
    LabelX message;
    LabelX actorName;
    Image actorImage;

    public MessageActor(String message, String actorName, String actorImage, FONT font) {
        this.message = new LabelX(message, getMessageStyle(font));
        this.actorName = new LabelX(actorName, getNameStyle(font));
        this.actorImage = new Image(TextureCache.getOrCreateR(actorImage));

        //on hover remove shader
        //could be different!
        align(Align.topLeft);

        //        add()

        addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
            }
        });
    }

    private LabelStyle getNameStyle(FONT font) {
        return StyleHolder.getSizedLabelStyle(font, 18);
    }

    private LabelStyle getMessageStyle(FONT font) {
        return StyleHolder.getSizedLabelStyle(font, 17);
    }

    @Override
    public void layout() {
        super.layout();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        //        ShaderMaster.drawWithCustomShader(this, DarkGrayscaleShader.getGrayscaleShader());
        super.draw(batch, parentAlpha);
    }
}

