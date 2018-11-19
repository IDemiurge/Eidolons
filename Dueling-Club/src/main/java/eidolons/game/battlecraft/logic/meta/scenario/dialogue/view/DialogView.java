package eidolons.game.battlecraft.logic.meta.scenario.dialogue.view;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.gui.LabelX;
import eidolons.libgdx.gui.panels.TablePanelX;
import main.system.auxiliary.StringMaster;
import main.system.graphics.FontMaster.FONT;

public class DialogView extends Group {
    private int time;
    private Image back; //what about THAT?
    private boolean done;
    private long currentTime = 0;

    FadeImageContainer portraitLeft; //IDEA zoom into the portrait sometimes!
    FadeImageContainer portraitRight; // or flash it with a shader to signify some emotion ... use dif borders

    DialogueScroll scroll;
    TablePanelX replyBox; //slots?

    public void update(SpeechDataSource dataSource){
        boolean left = true;
        FadeImageContainer portrait = left ? portraitLeft : portraitRight;
//        prev = portrait.getPrevious();
        portrait.setImage(StringMaster.getAppendedImageFile(dataSource.getActorImage(),
         dataSource.getImageSuffix()));
        scroll.append(dataSource.getMessage(), dataSource.getActorName(), dataSource.getActorImage());
//TODO info about fx! "Gain 50 xp"
    }

    public DialogView(int time, boolean skippable, TextureRegion backTexture, String message, TextureRegion portraitTexture) {
        this.time = time;
        skippable = time <= 0 || skippable;
        if (skippable) {
            addCaptureListener(new InputListener() {
                @Override
                public boolean keyDown(InputEvent event, int keycode) {
                    done = true;
                    return false;
                }
            });
        }

        if (backTexture != null) {
            this.back = new Image(backTexture);
            final float width = back.getWidth();
            final float height = back.getHeight();

            final int screenW = GdxMaster.getWidth();
            final int screenH = GdxMaster.getHeight();

            float x, y;

            if (width > screenW) {
                x = (width - screenW) / 2;
            } else {
                x = screenW / 2 - width / 2;
            }

            if (height > screenH) {
                y = (height - screenH) / 2;
            } else {
                y = screenH / 2 - height / 2;
            }

            back.setPosition(x, y);
            addActor(back);
        }
boolean lightweight;
boolean upsideDown;


        LabelX msgLabel = new LabelX();
        msgLabel.setStyle(StyleHolder.getSizedLabelStyle(FONT.MAIN, 20));
//        SmartButton response = new SmartButton(STD_BUTTON.DIALOGUE, ()-> respond(option), FONT.MAIN, 20, GdxColorMaster.PALE_GOLD)
//TiledNinePatchGenerator.getOrCreateNinePatch()
//        dialogueBox.setBackground();

    }

    public boolean isDone() {
        return done;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (done) return;
        if (time > 0) {
            currentTime += (int) (delta * 1000);

            if (time <= currentTime) {
                done = true;
            }
        }
    }
}
