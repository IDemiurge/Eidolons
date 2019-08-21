package eidolons.game.battlecraft.logic.meta.scenario.dialogue.view;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.bf.generic.ImageContainer;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.panels.InnerScrollContainer;
import eidolons.libgdx.gui.panels.TablePanelX;
import main.system.graphics.FontMaster.FONT;

/**
 * Created by JustMe on 11/16/2018.
 */
public class DialogueScroll extends TablePanelX {

    ScrollPane scrollPane;
    private TablePanelX<Actor> inner;
    LabelStyle currentStyle;
    public static final float WIDTH = 1200;
    public static final String BG = "ui/components/generic/dialogue/grunge bg full.png";
    private ImageContainer bg;

    public DialogueScroll() {
        init();
    }

    public void init() {
        setSize(WIDTH, DialogueView.HEIGHT - 50);
        addActor(bg = new ImageContainer( BG));

        inner = (new TablePanelX());
        inner.setFillParent(true);
        inner.setLayoutEnabled(true);
        inner.pack();
//        inner.setFixedMinSize(true);
//        inner.setFixedSize(true);
        inner.setWidth(WIDTH);
//        inner.setBackground(NinePatchFactory.getHqDrawable());
//        setBackground(NinePatchFactory.getLightDecorPanelFilledDrawable());


        InnerScrollContainer<Actor> innerScrollContainer = new InnerScrollContainer<>();
        innerScrollContainer.setActor(inner);
        innerScrollContainer.setX(0);
        innerScrollContainer.setY(0);

        add(scrollPane = new ScrollPane(innerScrollContainer){
            @Override
            public void fling(float flingTime, float velocityX, float velocityY) {
                super.fling(flingTime*5, velocityX/5, velocityY/5);
            }

        });
        scrollPane.setStyle(StyleHolder.getScrollStyle());
        scrollPane.setClamp(true);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(true);
        setFixedMinSize(true);
        scrollPane.setFillParent(true);
        scrollPane.setFlingTime(4);
        scrollPane.setupOverscroll( 50, 30, 100);
        scrollPane.setVariableSizeKnobs(false);
        scrollPane.setScrollY(-500);

//        this.setTouchable(Touchable.enabled);
//        setClip(true);

    }

    @Override
    protected Drawable getDefaultBackground() {
        return new NinePatchDrawable(NinePatchFactory.getLightDecorPanelDrawable());
    }


    public DialogueMessage append(String message, String actorName, String actorImage, boolean appendedMessage) {
        //actually, we'll need to append imgs too, eh?

        DialogueMessage dialogueMessage = new DialogueMessage(message, actorName, actorImage,
                getFontType(), getWidth() * 0.85f,appendedMessage);
        dialogueMessage.fadeIn();
        inner.add(dialogueMessage).center();
        inner.row();
//        inner.getRows()
        scrollPane.setScrollY(inner.getPrefHeight());
        return dialogueMessage;
    }

    protected FONT getFontType() {
        return FONT.METAMORPH;
    }


    protected int getFontSize() {
        return 19;
    }

    protected FONT getFontStyle() {
        return FONT.MAIN;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        inner.setY(0);
        bg.setPosition(-50, -100);
        scrollPane.setY(0);
        super.draw(batch, parentAlpha);
        getStage().setScrollFocus(scrollPane);
    }
}
