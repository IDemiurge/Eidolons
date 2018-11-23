package eidolons.game.battlecraft.logic.meta.scenario.dialogue.view;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.gui.LabelX;
import eidolons.libgdx.gui.panels.TablePanelX;
import main.system.auxiliary.StringMaster;
import main.system.graphics.FontMaster.FONT;

public class DialogView extends TablePanelX {
    FadeImageContainer portraitLeft; //IDEA zoom into the portrait sometimes!
    FadeImageContainer portraitRight; // or flash it with a shader to signify some emotion ... use dif borders
    DialogueScroll scroll;
    TablePanelX replyBox; //slots?

    private int time;
    private long currentTime = 0;
    private boolean done;

    public DialogView(int time, boolean skippable) {
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

        boolean lightweight;
        boolean upsideDown;

        LabelX msgLabel = new LabelX();
        msgLabel.setStyle(StyleHolder.getSizedLabelStyle(FONT.MAIN, 20));

//        TiledNinePatchGenerator.getOrCreateNinePatch()
//        dialogueBox.setBackground();

//        /decoar
        TablePanelX<Actor> textArea = new TablePanelX<>();



        add(portraitLeft);
        add(textArea);
        add(portraitRight);

    }

    public void update(SpeechDataSource dataSource) {
        boolean left = true;
        FadeImageContainer portrait = left ? portraitLeft : portraitRight;
        //        prev = portrait.getPrevious();
        portrait.setImage(StringMaster.getAppendedImageFile(dataSource.getActorImage(),
         dataSource.getImageSuffix()));
        scroll.append(dataSource.getMessage(), dataSource.getActorName(), dataSource.getActorImage());
        //TODO info about fx! "Gain 50 xp"

//        updateResponses(dataSource.getSpeech().getChildren())

//        SmartButton response = new SmartButton(STD_BUTTON.DIALOGUE,
//         () -> respond(option), FONT.MAIN, 20, GdxColorMaster.PALE_GOLD);
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
