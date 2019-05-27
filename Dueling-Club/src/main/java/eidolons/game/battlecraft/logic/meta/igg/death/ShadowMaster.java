package eidolons.game.battlecraft.logic.meta.igg.death;

import com.badlogic.gdx.graphics.g2d.Animation;
import eidolons.ability.conditions.special.ClearShotCondition;
import eidolons.ability.effects.oneshot.unit.SummonEffect;
import eidolons.content.PARAMS;
import eidolons.entity.obj.DC_Cell;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.dungeon.universal.Positioner;
import eidolons.game.battlecraft.logic.meta.igg.IGG_Images;
import eidolons.game.battlecraft.logic.meta.igg.IGG_Meta;
import eidolons.game.battlecraft.logic.meta.igg.IGG_MetaMaster;
import eidolons.game.battlecraft.logic.meta.igg.event.TipMessageSource;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameHandler;
import eidolons.game.battlecraft.logic.meta.universal.MetaGameMaster;
import eidolons.game.battlecraft.rules.round.UnconsciousRule;
import eidolons.game.core.CombatLoop;
import eidolons.game.core.EUtils;
import eidolons.game.core.Eidolons;
import eidolons.game.module.dungeoncrawl.explore.ExplorationMaster;
import eidolons.libgdx.anims.main.AnimMaster;
import eidolons.libgdx.anims.std.sprite.ShadowAnimation;
import eidolons.system.audio.DC_SoundMaster;
import main.entity.Entity;
import main.entity.Ref;
import main.game.bf.Coordinates;
import main.game.bf.directions.FACING_DIRECTION;
import main.game.logic.event.Event;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.log.LogMaster;
import main.system.sound.SoundMaster;
import main.system.threading.WaitMaster;

import static eidolons.game.battlecraft.logic.meta.igg.event.TipMessageMaster.TIP_MESSAGE.*;

public class ShadowMaster extends MetaGameHandler<IGG_Meta> {

    static float  timesThisHeroFell = 0;
    private int timeLeft;
    private static Unit shade;
    private static   boolean shadowAlive;

    public ShadowMaster(MetaGameMaster master) {
        super(master);
    }

    public static boolean isOn() {
        return true;
    }

    public static boolean isShadowAlive() {
        return shadowAlive;
    }

    public static boolean checkCheatDeath() {
        if (timesThisHeroFell==0){
            timesThisHeroFell++;
            return true;
        }
        return false;
    }

    public static Unit getShadowUnit() {
        return shade;
    }

    public boolean death() {
//        if (timesThisHeroFell==0){
//            return false;
//        }
        timesThisHeroFell = 0;
        return true;
    }

    public void heroFell(Event event) {
        getGame().getLoop().setPaused(true);
        AnimMaster.waitForAnimations(null);
        DC_SoundMaster.playStandardSound(SoundMaster.STD_SOUNDS.NEW__ENTER);
        ShadowAnimation anim = new ShadowAnimation(true, (Entity) event.getRef().getActive(),
                ()-> afterFall(event));
        GuiEventManager.trigger(GuiEventType. CUSTOM_ANIMATION, anim);



    }

    private void afterFall(Event event) {
        DC_SoundMaster.playStandardSound(SoundMaster.STD_SOUNDS.NEW__BATTLE_START2);
        GuiEventManager.trigger(GuiEventType.FADE_OUT_AND_BACK, 2);
        WaitMaster.WAIT(1200);

        if (ExplorationMaster.isExplorationOn()){
            // if we just fell as the combat was being finished... from poison or so
            restoreHero(event);
            LogMaster.log(1,"SHADOW: SHADOW: fall prevented; restoreHero! "+event );
            EUtils.showInfoText(true, RandomWizard.random()? "On the edge of consciousness...":"A narrow escape...");
            return;
        }
        timeLeft = calcTimeLeft(event);
        timesThisHeroFell++;
        GuiEventManager.trigger(GuiEventType.TIP_MESSAGE, new TipMessageSource(
                UNCONSCIOUS.message,
                IGG_Images.SHADOW + timeLeft, "I Am Become Death", false, () ->
                summonShade(event)));
    }

    public void timeElapsed(Event event) {
        if (!shadowAlive)
            return;
        timeLeft -= event.getRef().getAmount();
        if (timeLeft <= 0) {
            outOfTime(event);
            return;
        }
        String msg = "[!] Shadow: " + timeLeft + " seconds left to finish combat";
        EUtils.showInfoText(true,msg);
    }

    public void annihilated(Event event) {
        dialogueFailed(event, false);
    }

    private void outOfTime(Event event) {
        dialogueFailed(event, true);
    }

    @Override
    public IGG_MetaMaster getMaster() {
        return (IGG_MetaMaster) super.getMaster();
    }

    private void unsummonShade(Event event, boolean defeat) {
        Eidolons.onThisOrNonGdxThread(() -> {
        LogMaster.log(1,"SHADOW: unsummonShade "+event );
        shade.removeFromGame();
        shadowAlive = false;
        GuiEventManager.trigger(GuiEventType.POST_PROCESSING_RESET);
        if (!defeat) {
            out();
            restoreHero(event);
        } else
                getMaster().getBattleMaster().getOutcomeManager().defeat(false, true);
            });
    }

    private void restoreHero(Event event) {
        LogMaster.log(1,"SHADOW: restoreHero "  );
        Unit hero = Eidolons.getMainHero();
        UnconsciousRule.unitRecovers(hero);
        hero.setParam(PARAMS.C_FOCUS, hero.getParam(PARAMS.STARTING_FOCUS));
        hero.resetDynamicParam(PARAMS.C_TOUGHNESS);
        GuiEventManager.trigger(GuiEventType.CAMERA_PAN_TO_UNIT, hero);
//        getMaster().getGame().getRules().getUnconsciousRule().checkUnitAnnihilated();
    }

    public void victory(Event event) {
        if (!shadowAlive)
            return;
        LogMaster.log(1,"SHADOW: victory " );
        AnimMaster.waitForAnimations(null);

        ShadowAnimation anim = new ShadowAnimation(false, (Entity) event.getRef().getActive(),
                ()-> afterVictory(event)){
            @Override
            protected Animation.PlayMode getPlayMode() {
                return Animation.PlayMode.REVERSED;
            }
        };
        GuiEventManager.trigger(GuiEventType. CUSTOM_ANIMATION, anim);

    }

    private void afterVictory(Event event) {
        String msg = SHADE_RESTORE.message;
//              btn="Into Evernight";
        String btn = "Return";
        GuiEventManager.trigger(GuiEventType.TIP_MESSAGE, new TipMessageSource(
                msg, IGG_Images.SHADOW, btn, false, () ->
                unsummonShade(event, false)));
    }

    private void dialogueFailed(Event event, boolean outOfTime) {
        if (!shadowAlive)
            return;

        shade.getGame().getLoop().setPaused(true);

        AnimMaster.waitForAnimations(null);
        String msg = outOfTime ? DEATH_SHADE_TIME.message : DEATH_SHADE
                .message;
        String btn = "Onward!";
        if (getMaster().getDefeatHandler().isNoLivesLeft()) {
            DC_SoundMaster.playStandardSound(SoundMaster.STD_SOUNDS.NEW__DEFEAT);
            msg = DEATH_SHADE_FINAL.message;
//              btn="Into Evernight";
            btn = "Enter the Void";
        }
        GuiEventManager.trigger(GuiEventType.TIP_MESSAGE, new TipMessageSource(
                msg, IGG_Images.SHADOW, btn, false, () ->
                unsummonShade(event, true)));
    }

    private void out() {

        if (getGame().getLoop() instanceof CombatLoop) {
            ((CombatLoop) getGame().getLoop()).endCombat();

        }
    }

    private void summonShade(Event event) {
        LogMaster.log(1,"SHADOW: summonShade "+event );

        Ref ref = event.getRef().getCopy();
        Coordinates c = ref.getSourceObj().getCoordinates();
        FACING_DIRECTION facing = Eidolons.getMainHero().getFacing();
        Coordinates finalC = c;
        c = Positioner.adjustCoordinate(shade,
                c.getAdjacentCoordinate(facing.flip().getDirection()), facing,
                c1 -> new ClearShotCondition().check(finalC, c1)&& finalC.dst(c1)<=1.5f);
        DC_Cell cell = getGame().getCellByCoordinate(c);

        ref.setTarget(cell.getId());

        new SummonEffect("Torment").apply(ref);
        shade = (Unit) ref.getObj(Ref.KEYS.SUMMONED);
        shade.setScion(true);
        GuiEventManager.trigger(GuiEventType.UNIT_CREATED, shade);
        GuiEventManager.trigger(GuiEventType.UPDATE_MAIN_HERO, shade);
//        GuiEventManager.trigger(GuiEventType.GAME_RESET, shade);
        shade.setDetectedByPlayer(true);
        getGame().getLoop().setPaused(false);
        //horrid sound!
        DC_SoundMaster.playStandardSound(SoundMaster.STD_SOUNDS.DEATH);

        ShadowAnimation anim = new ShadowAnimation(false , (Entity) event.getRef().getActive(),
                ()-> {
                    GuiEventManager.trigger(GuiEventType.POST_PROCESSING);
                    DC_SoundMaster.playStandardSound(SoundMaster.STD_SOUNDS.NEW__BATTLE_START);
                });
        GuiEventManager.trigger(GuiEventType. CUSTOM_ANIMATION, anim);
        shadowAlive = true;
        return;
    }

    private  int calcTimeLeft(Event event) {
        return (int) (Math.round(Math.pow(3, 2 - timesThisHeroFell)) + 5 - timesThisHeroFell * 2)
                + RandomWizard.getRandomInt(8);  //20, 12, 8, ...
//        return (int) (Math.round(Math.pow(4, 2 - timesThisHeroFell ))+4 - timesThisHeroFell);  //20, 7, 3, ...
    }

    public int getTimeLeft() {
        return timeLeft;
    }
}
