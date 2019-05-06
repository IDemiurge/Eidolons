package eidolons.game.battlecraft.logic.meta.igg.death;

import eidolons.ability.effects.oneshot.unit.SummonEffect;
import eidolons.entity.obj.DC_Cell;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.dungeon.universal.Positioner;
import eidolons.game.battlecraft.logic.meta.igg.IGG_Images;
import eidolons.game.battlecraft.logic.meta.igg.IGG_Meta;
import eidolons.game.battlecraft.logic.meta.igg.IGG_MetaMaster;
import eidolons.game.battlecraft.logic.meta.igg.event.TipMessageMaster;
import eidolons.game.battlecraft.logic.meta.igg.event.TipMessageSource;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameHandler;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.battlecraft.rules.action.StackingRule;
import eidolons.game.core.Eidolons;
import eidolons.system.audio.DC_SoundMaster;
import main.entity.Ref;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;
import main.game.logic.event.Event;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.sound.SoundMaster;

public class ShadowMaster extends MetaGameHandler<IGG_Meta> {

    float timesThisHeroFell = 0;
    private int timeLeft;
    private Unit shade;


    public ShadowMaster(MetaGameMaster master) {
        super(master);
    }

    public void death() {
        timesThisHeroFell = 0;
    }

    public void fall(Event event) {
        timesThisHeroFell++;
        getGame().getLoop().setPaused(true);
        timeLeft = getTimeLeft(event);
        GuiEventManager.trigger(GuiEventType.TIP_MESSAGE, new TipMessageSource(
                TipMessageMaster.TIP_MESSAGE.UNCONSCIOUS.message,
                IGG_Images.SHADOW + timeLeft, "I Am Become Death", false, () ->
                summonShade(event)));
        //play sprite anim on a spot?

        // apply ability?

//        new SummonEffect()

        //how to give control?
    }

    private int getTimeLeft(Event event) {
        return (int) (Math.round(Math.pow(3, 2 - timesThisHeroFell)) + 11 - timesThisHeroFell * 2);  //20, 12, 8, ...
//        return (int) (Math.round(Math.pow(4, 2 - timesThisHeroFell ))+4 - timesThisHeroFell);  //20, 7, 3, ...
    }

    private void unsummonShade(Event event) {
        // it is over... the pain is unbearable


        DC_SoundMaster.playStandardSound(SoundMaster.STD_SOUNDS.NEW__DEFEAT);
        GuiEventManager.trigger(GuiEventType.POST_PROCESSING_RESET);
    }

    public void timeElapsed(Event event) {
    }
    private void summonShade(Event event) {
        Ref ref = event.getRef().getCopy();
        Coordinates c = ref.getSourceObj().getCoordinates();
        FACING_DIRECTION facing = Eidolons.getMainHero().getFacing();
        c = Positioner.adjustCoordinate(shade, c.getAdjacentCoordinate(facing.flip().getDirection()), facing);
        DC_Cell cell = getGame().getCellByCoordinate(c);

        ref.setTarget(cell.getId());

        new SummonEffect("Torment").apply(ref);
        shade = (Unit) ref.getObj(Ref.KEYS.SUMMONED);
        shade.setScion(true);

        getGame().getLoop().setPaused(false);
        //horrid sound!
        DC_SoundMaster.playStandardSound(SoundMaster.STD_SOUNDS.DEATH);

        GuiEventManager.trigger(GuiEventType.POST_PROCESSING);
        return;
    }

}
