package eidolons.game.netherflame.main.soul;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.SpeechExecutor;
import eidolons.game.core.EUtils;
import eidolons.game.core.Eidolons;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.bf.SuperActor;
import eidolons.libgdx.gui.LabelX;
import eidolons.libgdx.gui.generic.btn.SmartButton;
import eidolons.libgdx.gui.panels.TablePanelX;
import eidolons.libgdx.gui.tooltips.DynamicTooltip;
import eidolons.system.audio.DC_SoundMaster;
import main.content.enums.GenericEnums;
import main.content.enums.entity.UnitEnums;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class SoulCounter extends SuperActor {
    private final TablePanelX<Actor> table;
    SmartButton btn;
    LabelX counter;
    int souls = 3;

    public SoulCounter() {
        addActor(table = new TablePanelX<>());
        // table.setY(-25);
        // table.setBackground(new FlipDrawable(Images.ZARK_TITLE, () -> false, () -> true));
        // table.addBackgroundActor(new ImageContainer(Images.ZARK_BOX_UPSIDE_DOWN));
        // table.addActor(btn = new SmartButton(ButtonStyled.STD_BUTTON.SOULS_BTN, () -> {
        //     GuiEventManager.trigger(GuiEventType.SOULS_CONSUMED);
        // }));
        Label.LabelStyle style = StyleHolder.newStyle(StyleHolder.getAVQLabelStyle(22));

        table.addActor(counter = new LabelX("", style));

        //animate on gain/lose
        GuiEventManager.bind(GuiEventType.SOULS_CONSUMED, p -> {
            if (Eidolons.getGame().getManager().getActiveObj() != Eidolons.getMainHero()) {
                EUtils.showInfoText("Cannot do this now");
                return;
            }
            DC_SoundMaster.playCueSound(GenericEnums.SOUND_CUE.ghost);
            Eidolons.getMainHero().addCounter(UnitEnums.COUNTER.Undying.getName(), souls + "");
            souls = 0;
            counter.setText("" + souls);
            SpeechExecutor.run("script=explosions(true)");
        });
        GuiEventManager.bind(GuiEventType.SOULS_CLAIMED, p -> {
            souls++;
            counter.setText("" + souls);
        });

        addListener(new DynamicTooltip(() ->
               SoulforceMaster.getTooltip()).getController());
        counter.setText("3");
    }

    @Override
    public void act(float delta) {
        // table.setY(-25);
        // table.setX(-table.getWidth()/2+11);
        counter.setText(EidolonLord.lord.getSoulforce() + "  /  " +
                EidolonLord.lord.getSoulforceMax());
        GdxMaster.center(counter);
        counter.setX(0);
        counter.setY(0);
        super.act(delta);
    }
}
