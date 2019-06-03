package eidolons.game.battlecraft.logic.meta.scenario.dialogue.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.panels.InnerScrollContainer;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.gui.tooltips.Tooltip;
import eidolons.libgdx.texture.Images;
import main.system.auxiliary.RandomWizard;
import main.system.graphics.FontMaster.FONT;

/**
 * Created by JustMe on 11/16/2018.
 */
public class DialogueScroll extends TablePanelX {

    ScrollPane scrollPane;
    private TablePanelX<Actor> inner;
    LabelStyle currentStyle;

    public DialogueScroll() {
        init();
    }

    public void init() {
        setSize(1200, 500);

        inner= (new TablePanelX());
        inner.setFillParent(true);
        inner.setLayoutEnabled(true);
        inner.pack();
//        inner.setFixedMinSize(true);
        inner.setFixedSize(true);
        inner.setWidth(1200);
        inner. setBackground(NinePatchFactory.getHqDrawable());
//        setBackground(NinePatchFactory.getLightDecorPanelFilledDrawable());


        InnerScrollContainer<Actor> innerScrollContainer = new InnerScrollContainer<>();
        innerScrollContainer.setActor(inner);
        innerScrollContainer.setX(0);
        innerScrollContainer.setY(0);

        add(scrollPane = new ScrollPane(innerScrollContainer));
        scrollPane.setStyle(StyleHolder.getScrollStyle());
        scrollPane.setClamp(true);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(true);
        setFixedMinSize(true);
        scrollPane.setFillParent(true);
        scrollPane.setFlingTime(2);
        scrollPane.setVariableSizeKnobs(false);
        scrollPane.setScrollY(-500);
//        this.setTouchable(Touchable.enabled);
//        setClip(true);

    }

    @Override
    protected Drawable getDefaultBackground() {
        return new NinePatchDrawable(NinePatchFactory.getLightDecorPanelDrawable());
    }


    public void append(String message, String actorName, String actorImage) {
        //actually, we'll need to append imgs too, eh?

        DialogueMessage dialogueMessage = new DialogueMessage(message, actorName, actorImage,
                FONT.MAIN, getWidth()*0.85f);
        dialogueMessage.fadeIn();
        inner.add(dialogueMessage).center();
        inner.row();
//        inner.getRows()
        scrollPane.setScrollY(inner.getPrefHeight());
//        getStage().setScrollFocus(scrollPane);
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
        scrollPane.setY(0);
        super.draw(batch, parentAlpha);
        getStage().setScrollFocus(scrollPane );
    }
}
