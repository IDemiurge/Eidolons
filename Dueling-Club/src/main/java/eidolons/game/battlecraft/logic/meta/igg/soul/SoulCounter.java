package eidolons.game.battlecraft.logic.meta.igg.soul;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.SpeechExecutor;
import eidolons.game.core.EUtils;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.GdxColorMaster;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.gui.LabelX;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.gui.generic.btn.ButtonStyled;
import eidolons.libgdx.gui.generic.btn.SmartButton;
import eidolons.libgdx.gui.tooltips.DynamicTooltip;
import eidolons.system.audio.DC_SoundMaster;
import main.content.enums.GenericEnums;
import main.content.enums.entity.UnitEnums;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class SoulCounter extends GroupX {
    SmartButton btn;
    LabelX counter;
    int souls = 3;

    public SoulCounter() {
        addActor(btn = new SmartButton(ButtonStyled.STD_BUTTON.SOULS_BTN, () -> {
            GuiEventManager.trigger(GuiEventType.SOULS_CONSUMED);
        }));
        Label.LabelStyle style = StyleHolder.newStyle(StyleHolder.getHqLabelStyle(20));
        style.fontColor = GdxColorMaster.CRIMSON;

        addActor(counter = new LabelX("", style));

        GuiEventManager.bind(GuiEventType.SOULS_CONSUMED, p -> {
            if (Eidolons.getGame().getManager().getActiveObj() != Eidolons.getMainHero()) {
                EUtils.showInfoText("Cannot do this now");
                return;
            }
            DC_SoundMaster.playCueSound(GenericEnums.SOUND_CUE.ghost);
            Eidolons.getMainHero().addCounter(UnitEnums.COUNTER.Undying.getName(), souls + "");
            if (EidolonsGame.BRIDGE) {
            EidolonLord.lord.soulforceGained(souls*10);
            }
            souls = 0;
            counter.setText("" + souls);
            SpeechExecutor.run("script=explosions(true)");
        });
        GuiEventManager.bind(GuiEventType.SOULS_CLAIMED, p -> {
            souls++;
            counter.setText("" + souls);
        });

        addListener(new DynamicTooltip(()-> "Turn Souls into Undying counters").getController());
        counter.setText("3");
    }

    @Override
    public void act(float delta) {
        setSize(btn.getWidth(), btn.getHeight());
        GdxMaster.center(counter);
        counter.setX((btn.getWidth()-counter.getPrefWidth())/2);
        super.act(delta);
    }
}
