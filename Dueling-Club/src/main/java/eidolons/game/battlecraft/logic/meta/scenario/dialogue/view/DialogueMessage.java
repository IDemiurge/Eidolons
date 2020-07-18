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
    private final boolean append;
    Actor message;
    LabelX actorName;
    Image actorImage;

    public DialogueMessage(String message, String actorName, String img, FONT font, float w, boolean append) {
//        w=w/3*2;
        if (actorName == null || message == null) {
            actorName = "Error";
            message = "Report me!..";
        }
        message = message.trim();
        this.append = append;
        this.actorImage = new Image(TextureCache.getRegionUV(img));

        if (actorName.isEmpty() && message.isEmpty()) {
            add(this.actorImage = new Image(TextureCache.getRegionUV(img))).
                    size(actorImage.getPrefWidth(), actorImage.getPrefHeight()).center().padLeft(24);
            return;
        }
        TablePanelX<Actor> textTable = new TablePanelX<>();
        if (!append) {
            add(actorImage).size(64, 64).top().padLeft(20).padTop(12);
            Cell<LabelX> cell = textTable.add(this.actorName = new LabelX(actorName, getNameStyle(font))).left();
            cell.setActorX(-20);
            textTable.row();
        }
        textTable.add(this.message = new TextBuilder(getMessageStyle(font)).addString(message).build(w).pad(20));
//        textTable.add(this.message = new LabelX(message, getMessageStyle(font)));

        if (!append)
            add(textTable).padTop(25).padLeft(20).padBottom(5);
        else
            add(textTable).padLeft(5);
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
        if (append) {
            setY(20);
        }
    }

    @Override
    public float getPrefHeight() {
        if (append) {
            return super.getPrefHeight() - (20);
        }
        return super.getPrefHeight();
    }

    @Override
    public float getHeight() {
        return super.getPrefHeight();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        //        ShaderMaster.drawWithCustomShader(this, DarkGrayscaleShader.getGrayscaleShader());
        setX(0);
        try {
            super.draw(batch, parentAlpha);
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
    }
}

