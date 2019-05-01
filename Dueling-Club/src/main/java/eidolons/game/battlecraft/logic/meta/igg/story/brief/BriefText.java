package eidolons.game.battlecraft.logic.meta.igg.story.brief;

import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.anims.ActorMaster;
import eidolons.libgdx.gui.LabelX;
import eidolons.libgdx.gui.generic.btn.ButtonStyled;
import eidolons.libgdx.gui.generic.btn.SmartButton;
import eidolons.libgdx.gui.panels.TablePanelX;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.RandomWizard;
import main.system.graphics.FontMaster;
import main.system.threading.WaitMaster;

public class BriefText extends TablePanelX {

    private String[] messages;
    private int i = 0;
    LabelX mainText;
    // fade text; new passage on continue - better than rolling it down?
    SmartButton continueBtn;

    public BriefText(float width, float height, String... messages) {
        super(width, height);
        this.messages = messages;
        addActor(mainText = new LabelX());
        mainText.setStyle(StyleHolder.getSizedLabelStyle(
                 FontMaster.FONT.SUPER_KNIGHT, 21));
        mainText.setWidth(width);

        addActor(continueBtn = new SmartButton("Continue", StyleHolder.getTextButtonStyle(ButtonStyled.STD_BUTTON.MENU,
                FontMaster.FONT.DARK, GdxColorMaster.getDefaultTextColor(), 20)));
        continueBtn.pack();
        continueBtn.setPosition(GdxMaster.centerWidth(continueBtn), -continueBtn.getHeight());
        continueBtn.setRunnable(() -> {
            nextMsg();
            GuiEventManager.trigger(GuiEventType.BRIEFING_NEXT);
        });

        nextMsg();

    }


    private void nextMsg() {
        if (i >= messages.length) {
            GuiEventManager.trigger(GuiEventType.BRIEFING_FINISHED);
            WaitMaster.receiveInput(WaitMaster.WAIT_OPERATIONS.BRIEFING_COMPLETE, true);
            return;
        }
        setUserObject(messages[i++]);
        continueBtn.makeActive();
    }

    @Override
    public void updateAct(float delta) {
        super.updateAct(delta);
        if (mainText.getActions().size != 0) {
            updateRequired = true;
            return;
        }

        if (mainText.getText().equals(getUserObject().toString())) {
            ActorMaster.addFadeOutAction(mainText, getFadeOutDur());
        } else {
            if (mainText.getColor().a == 0) {
                mainText.setText(getUserObject().toString());
            }
            mainText.setText(getUserObject().toString());
            ActorMaster.addFadeInAction(mainText, getFadeInDur());
        }
        mainText.pack();
        pack();
        setHeight(mainText.getHeight()*2);
        mainText.setX(GdxMaster.centerWidth(mainText));
        mainText.setY(0);
        setX(GdxMaster.centerWidth(this));
    }

    private float getFadeOutDur() {
        return 2f;
    }

    private float getFadeInDur() {
        return 3f;
    }

}
