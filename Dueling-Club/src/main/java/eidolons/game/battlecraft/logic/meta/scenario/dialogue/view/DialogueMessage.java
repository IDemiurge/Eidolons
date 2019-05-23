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
import eidolons.libgdx.gui.panels.dc.logpanel.text.TextBuilder;
import eidolons.libgdx.texture.TextureCache;
import main.system.graphics.FontMaster.FONT;

/**
 * Created by JustMe on 11/16/2018.
 */
public class DialogueMessage extends TablePanelX {
    Actor message;
    LabelX actorName;
    Image actorImage;

    public DialogueMessage(String message, String actorName, String img, FONT font, float w) {
        if (actorName == null || message == null) {
            actorName = "Error";
            message = "Report me!..";
        }

        this.actorImage = new Image(TextureCache.getOrCreateR(img));

        if (actorName.isEmpty() && message.isEmpty()) {
            add(this.actorImage = new Image(TextureCache.getOrCreateR(img))).size(actorImage.getPrefWidth(), actorImage.getPrefHeight()).center();
            return;
        } else {
            add(actorImage).size(64, 64).pad(20);
        }
        TablePanelX<Actor> textTable = new TablePanelX<>();
        textTable.add(this.actorName = new LabelX(actorName, getNameStyle(font))).pad(20) .left().row();
        textTable.add(this.message = new TextBuilder(getMessageStyle(font)).addString(message).build(w).pad(20));
//        textTable.add(this.message = new LabelX(message, getMessageStyle(font)));
        add(textTable).pad(20);
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
        setX(0);
        super.draw(batch, parentAlpha);
    }
}

