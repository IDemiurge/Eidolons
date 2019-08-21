package eidolons.game.battlecraft.logic.meta.scenario.dialogue.view;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
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

    public DialogueMessage(String message, String actorName, String img, FONT font, float w, boolean append) {
//        w=w/3*2;
        if (actorName == null || message == null) {
            actorName = "Error";
            message = "Report me!..";
        }
        this.actorImage = new Image(TextureCache.getOrCreateR(img));

        if (actorName.isEmpty() && message.isEmpty()) {
            add(this.actorImage = new Image(TextureCache.getOrCreateR(img))).
                    size(actorImage.getPrefWidth(), actorImage.getPrefHeight()).center().padLeft(24);
            return;
        } else {
            if (!append)
                add(actorImage).size(64, 64).top().padLeft(20).padTop(12);
        }
        message = message.trim();
        TablePanelX<Actor> textTable = new TablePanelX<>();
        Cell<LabelX> cell = textTable.add(this.actorName = new LabelX(actorName, getNameStyle(font))).left();
        cell.setActorX(-20);
        textTable.row();
        textTable.add(this.message = new TextBuilder(getMessageStyle(font)).addString(message).build(w).pad(20));
//        textTable.add(this.message = new LabelX(message, getMessageStyle(font)));

        if (!append)
            add(textTable).pad(20);
        else
            add(textTable).pad(10);
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
        return StyleHolder.getSizedLabelStyle(FONT.SUPER_KNIGHT, 22);
    }

    private LabelStyle getMessageStyle(FONT font) {
        return StyleHolder.getSizedLabelStyle(font, 21);
    }

    @Override
    public void layout() {
        super.layout();
    }

    @Override
    public float getPrefHeight() {
        return super.getPrefHeight();
    }

    @Override
    public float getHeight() {
        return super.getHeight();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        //        ShaderMaster.drawWithCustomShader(this, DarkGrayscaleShader.getGrayscaleShader());
        setX(0);
        super.draw(batch, parentAlpha);
    }
}

