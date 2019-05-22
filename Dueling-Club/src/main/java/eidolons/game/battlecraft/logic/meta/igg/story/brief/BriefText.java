package eidolons.game.battlecraft.logic.meta.igg.story.brief;

import com.badlogic.gdx.graphics.g2d.Batch;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.anims.ActorMaster;
import eidolons.libgdx.gui.LabelX;
import eidolons.libgdx.gui.NinePatchFactory;
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
        setSize(1000, 250);
        setBackground(NinePatchFactory.getHqDrawable());
        addActor(mainText = new LabelX());
        mainText.setStyle(StyleHolder.getSizedLabelStyle(
                FontMaster.FONT.MAGIC, 21));
        mainText.setWidth(width);

        addActor(continueBtn = new SmartButton("Continue", StyleHolder.getTextButtonStyle(ButtonStyled.STD_BUTTON.MENU,
                FontMaster.FONT.DARK, GdxColorMaster.getDefaultTextColor(), 20)));
        continueBtn.pack();
        continueBtn.setPosition(GdxMaster.centerWidth(continueBtn), 0);
        continueBtn.setRunnable(() -> {
            nextMsg();
        });

//        nextMsg();
        if (messages.length > 0) {
            setUserObject(messages[i++]);
            continueBtn.makeActive();
            GuiEventManager.trigger(GuiEventType.BRIEFING_NEXT);
        }
    }

    public void setMessages(String[] messages) {
        this.messages = messages;
    }

    protected void nextMsg() {
        if (isDone()) {
            GuiEventManager.trigger(GuiEventType.BRIEFING_FINISHED);
            WaitMaster.receiveInput(WaitMaster.WAIT_OPERATIONS.BRIEFING_COMPLETE, true);
            return;
        }
        GuiEventManager.trigger(GuiEventType.BRIEFING_NEXT);
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
            ActorMaster.addFadeOutAction(this, getFadeOutDur());
        } else {
            if (mainText.getColor().a == 0) {
                mainText.setText(getUserObject().toString());
            }
            mainText.setText(getUserObject().toString());
            ActorMaster.addFadeInAction(this, getFadeInDur());
        }
        mainText.setSize(800, 250);
        continueBtn.makeActive();
//        setHeight(mainText.getHeight() * 2);
        mainText.setX(GdxMaster.centerWidth(mainText));
        mainText.setY(0);
//         setX(0);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
//        setY(150);
//        setX(GdxMaster.centerWidth(this));
        super.draw(batch, parentAlpha);
        continueBtn.setChecked(false);
        continueBtn.setDisabled(false);
//        continueBtn.setPosition(GdxMaster.centerWidth(continueBtn), 0);
    }

    private float getFadeOutDur() {
        return 2f;
    }

    private float getFadeInDur() {
        return 3f;
    }

    public boolean isDone() {
        return i >= messages.length;
    }
}
