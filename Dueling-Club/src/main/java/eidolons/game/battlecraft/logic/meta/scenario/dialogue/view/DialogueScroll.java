package eidolons.game.battlecraft.logic.meta.scenario.dialogue.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import eidolons.libgdx.GDX;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.gui.panels.ScrollPanel;
import eidolons.libgdx.gui.panels.dc.logpanel.text.Message;
import eidolons.libgdx.gui.panels.dc.logpanel.text.ScrollTextWrapper;
import eidolons.libgdx.gui.panels.dc.logpanel.text.TextBuilder;
import eidolons.libgdx.texture.Images;
import eidolons.system.options.GameplayOptions;
import eidolons.system.options.OptionsMaster;
import main.system.auxiliary.RandomWizard;
import main.system.graphics.FontMaster.FONT;

/**
 * Created by JustMe on 11/16/2018.
 */
public class DialogueScroll extends ScrollTextWrapper{
    LabelStyle currentStyle;

    public DialogueScroll() {
        super(1000, 500);
    }


    @Override
    protected float getDefaultHeight() {
        return  (500);
    }

    @Override
    protected float getDefaultWidth() {
        return  (1000);
    }
    @Override
    protected Drawable getNinePatch() {
        return super.getNinePatch();
    }

    @Override
    protected TextBuilder getTextBuilder() {
        return super.getTextBuilder();
    }

    public void append(String message, String actorName, String actorImage) {
    //actually, we'll need to append imgs too, eh?

        DialogueMessage dialogueMessage =new DialogueMessage(message, actorName, actorImage,
         FONT.MAGIC, getDefaultWidth());

        scrollPanel.addElement(dialogueMessage);
    }


    protected ScrollPanel<DialogueMessage> createScrollPanel() {
        return new TextScroll() {

            @Override
            protected void initAlignment() {
                left().bottom();
            }

            @Override
            protected void pad(ScrollPanel scrollPanel) {
                padScroll(scrollPanel);
            }

            @Override
            protected boolean isAlwaysScrolled() {
                return isScrolledAlways();
            }

            @Override
            public int getDefaultOffsetY() {
                return 0;
            }
        };
    }

    @Override
    protected boolean isScrolledAlways() {
        return !super.isScrolledAlways();
    }

    @Override
    protected int getFontSize() {
        return 19;
    }

    @Override
    protected FONT getFontStyle() {
        return FONT.MAIN;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (Gdx.input.isKeyPressed(Input.Keys.TAB)) {
            append(Images.WEAVE_LINK+"\n\n\n", "Hero", Images.SEPARATOR);
        }
        ScrollPane pane = (ScrollPane) scrollPanel.getActor();
        pane.setScrollPercentY(RandomWizard.getRandomFloat());
        getStage().setScrollFocus( scrollPanel.getActor());
    }

    @Override
    protected float getTextLineWidth() {
        return getWidth() * 0.75f;
    }
}
