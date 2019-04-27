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
import main.system.graphics.FontMaster;

public class BriefText extends TablePanelX {

    private   String[] messages;
    private   int i=0;
    LabelX mainText;
    // fade text; new passage on continue - better than rolling it down?
    SmartButton continueBtn;

    public BriefText(float width, float height, String... messages) {
        super(width, height);
        mainText = new LabelX();
        mainText.setStyle(StyleHolder.getSizedLabelStyle(FontMaster.FONT.SUPER_KNIGHT, 20));
        addActor(mainText);

        mainText.setPosition(GdxMaster.centerWidth(mainText), GdxMaster.centerHeight(mainText));
        this.messages=messages;
        addActor(continueBtn = new SmartButton("Continue", StyleHolder.getTextButtonStyle(ButtonStyled.STD_BUTTON.MENU,
                FontMaster.FONT.SUPER_KNIGHT, GdxColorMaster.getDefaultTextColor(), 20)));

        continueBtn.setPosition(GdxMaster.centerWidth(continueBtn), 0);
        continueBtn.setRunnable(()->{
            nextMsg();
            GuiEventManager.trigger(GuiEventType.BRIEFING_NEXT);
        });
        setUserObject(messages[0]);
    }

    public BriefText() {
        // 1/3 width
    }

    private void nextMsg() {
        setUserObject(messages[i++]);
    }

    @Override
    public void updateAct(float delta) {
        super.updateAct(delta);
        if (mainText.getActions().size!=0){
            updateRequired=true;
            return;
        }

        if (mainText.getText().equals(getUserObject().toString())) {
            ActorMaster.addFadeOutAction(mainText, getFadeOutDur());
        } else {
            if (mainText.getColor().a==0) {
                mainText.setText(getUserObject().toString());
            }
            mainText.setText(getUserObject().toString());
            ActorMaster.addFadeInAction(mainText, getFadeInDur());
        }
    }

    private float getFadeOutDur() {
        return 2f;
    }

    private float getFadeInDur() {
        return 3f;
    }

}
