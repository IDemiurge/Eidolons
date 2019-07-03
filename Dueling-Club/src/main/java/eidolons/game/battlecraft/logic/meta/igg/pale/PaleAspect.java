package eidolons.game.battlecraft.logic.meta.igg.pale;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.battlecraft.logic.battle.universal.DC_Player;
import eidolons.game.battlecraft.logic.dungeon.universal.Positioner;
import eidolons.game.battlecraft.logic.meta.igg.death.ChainHero;
import eidolons.game.battlecraft.logic.meta.igg.soul.EidolonLord;
import eidolons.game.battlecraft.rules.action.StackingRule;
import eidolons.game.core.ActionInput;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.herocreator.logic.spells.SpellMaster;
import eidolons.libgdx.shaders.post.PostFxUpdater;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.threading.WaitMaster;

public class PaleAspect {


    private static final String AVATAR_TYPE = "Anphis Ar Keserim";
    public static boolean ON;
    private static Unit avatar;
    private static int d;

    /**
     * transformation rules
     * <p>
     * movement
     * <p>
     * vision
     * <p>
     * avatar
     * <p>
     * enter/exit
     */

    private static Unit createPaleAvatar() {
        avatar = (Unit) DC_Game.game.createUnit(DataManager.getType(AVATAR_TYPE, DC_TYPE.CHARS),
                Eidolons.getMainHero().getX(),
                Eidolons.getMainHero().getY(), DC_Game.game.getPlayer(true));
        return avatar;
    }


    public static void togglePale() {
        if (ON) {
            exitPale();
        } else {
            enterPale();
        }
    }

    public static void exitPale() {
        ON = false;
        GuiEventManager.trigger(GuiEventType.POST_PROCESSING_RESET);

        Eidolons.resetMainHero();
    }

    public static void enterPale() {
        ON = true;
        d = 0;
        GuiEventManager.trigger(GuiEventType.POST_PROCESSING, PostFxUpdater.POST_FX_TEMPLATE.PALE_ASPECT);
        if (avatar == null) {
            avatar = createPaleAvatar();
            avatar.setPale(true);
            avatar.getGame().getPaleMaster().tryAddUnit(avatar);
            avatar.getGame().getMaster().remove(avatar, true);
            //reset vision etc
            GuiEventManager.trigger(GuiEventType.UNIT_CREATED, avatar);
            for (ChainHero hero : EidolonLord.lord.getChain().getHeroes()) {
                if (!hero.getUnit().isLeader()) {
                    GuiEventManager.trigger(GuiEventType.UNIT_CREATED, hero.getUnit());
                }
            }
        }

        //GuiEventManager.trigger(GuiEventType. )

        for (ChainHero hero : EidolonLord.lord.getChain().getHeroes()) {
            shadowLeapToLocation(hero.getUnit(), true);
        }
        shadowLeapToLocation(avatar, false);

        Eidolons.bufferMainHero();
        Eidolons.setMainHero(avatar);

    }

    private static void shadowLeapToLocation(Unit unit, boolean adjacent) {
        Coordinates c = Eidolons.getMainHero().getCoordinates();
        if (adjacent) {
            c = c.getAdjacentCoordinate(DIRECTION.DIAGONAL[d]);
            if (c == null) {
                c = Positioner.adjustCoordinate(c, FACING_DIRECTION.NORTH);
                d++;
            }
        }
        if (unit.getSpell("Leap into Darkness") == null) {
            SpellMaster.addVerbatimSpell(unit, DataManager.getType("Leap into Darkness", DC_TYPE.SPELLS));
            unit.reset();
            unit.initSpells(true);
        }
        if (unit.getSpell("Leap into Darkness") == null) {
            main.system.auxiliary.log.LogMaster.log(1, "PALE: failed to init LEAP" + unit);
            unit.setCoordinates(c);
            return;
        }

        unit.getGame().getLoop().actionInput(new ActionInput(unit.getSpell("Leap into Darkness"),
                avatar.getGame().getCellByCoordinate(c)));
//        WaitMaster.receiveInput(WaitMaster.WAIT_OPERATIONS.ACTION_INPUT, new ActionInput(unit.getSpell("Leap into Darkness"),
//                avatar.getGame().getCellByCoordinate(c)));
    }

}
