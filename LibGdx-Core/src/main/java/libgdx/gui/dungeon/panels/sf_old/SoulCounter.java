package libgdx.gui.dungeon.panels.sf_old;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import eidolons.game.battlecraft.logic.meta.scenario.dialogue.speech.SpeechExecutor;
import eidolons.game.core.EUtils;
import eidolons.game.core.Core;
import eidolons.netherflame.lord.EidolonLord;
import eidolons.netherflame.eidolon.chain.SoulforceMaster;
import libgdx.GdxMaster;
import libgdx.StyleHolder;
import libgdx.bf.SuperActor;
import libgdx.gui.LabelX;
import libgdx.gui.generic.btn.SmartTextButton;
import libgdx.gui.dungeon.panels.TablePanelX;
import libgdx.gui.dungeon.tooltips.DynamicTooltip;
import eidolons.system.audio.DC_SoundMaster;
import main.content.enums.GenericEnums;
import main.content.enums.entity.EffectEnums;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class SoulCounter extends SuperActor {
    SmartTextButton btn;
    LabelX counter;
    int souls = 3;

    public SoulCounter() {
        TablePanelX<Actor> table;
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
            if (Core.getGame().getManager().getActiveObj() != Core.getMainHero()) {
                EUtils.showInfoText("Cannot do this now");
                return;
            }
            DC_SoundMaster.playCueSound(GenericEnums.SOUND_CUE.ghost);
            Core.getMainHero().addCounter(EffectEnums.COUNTER.Undying.getName(), souls + "");
            souls = 0;
            counter.setText("" + souls);
            SpeechExecutor.run("script=explosions(true)");
        });
        GuiEventManager.bind(GuiEventType.SOULS_CLAIMED, p -> {
            souls++;
            counter.setText("" + souls);
        });

        addListener(new DynamicTooltip(SoulforceMaster::getTooltip).getController());
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
