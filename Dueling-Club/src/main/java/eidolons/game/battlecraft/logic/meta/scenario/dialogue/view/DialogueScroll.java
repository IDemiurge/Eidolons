package eidolons.game.battlecraft.logic.meta.scenario.dialogue.view;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import eidolons.libgdx.GDX;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.gui.panels.dc.logpanel.text.ScrollTextWrapper;
import main.system.graphics.FontMaster.FONT;

/**
 * Created by JustMe on 11/16/2018.
 */
public class DialogueScroll extends ScrollTextWrapper{
    LabelStyle currentStyle;

    public void append(String message, String actorName, String actorImage) {
    //actually, we'll need to append imgs too, eh?

        MessageActor messageActor=new MessageActor(message, actorImage, actorName,
         FONT.MAGIC);
        scrollPanel.addElement(messageActor);
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
    }

    @Override
    protected float getDefaultHeight() {
        return GDX.height(500);
    }

    @Override
    protected float getTextLineWidth() {
        return getWidth() * 0.75f;
    }

    @Override
    protected float getDefaultWidth() {
        return GdxMaster.adjustWidth(900);
    }
}
