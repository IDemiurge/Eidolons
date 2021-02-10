package eidolons.game.netherflame.main.pale;

import eidolons.entity.obj.unit.Unit;
import eidolons.game.EidolonsGame;
import eidolons.game.battlecraft.logic.dungeon.universal.Positioner;
import eidolons.game.core.ActionInput;
import eidolons.game.core.Eidolons;
import eidolons.game.core.game.DC_Game;
import eidolons.game.module.herocreator.logic.spells.SpellMaster;
import eidolons.game.netherflame.additional.IGG_Demo;
import eidolons.game.netherflame.main.death.ChainHero;
import eidolons.game.netherflame.main.soul.EidolonLord;
import eidolons.libgdx.shaders.post.PostFxUpdater;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.game.bf.Coordinates;
import main.game.bf.directions.DIRECTION;
import main.game.bf.directions.FACING_DIRECTION;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import static main.system.auxiliary.log.LogMaster.important;
import static main.system.auxiliary.log.LogMaster.log;

public class PaleAspect {
    private static final String AVATAR_TYPE = IGG_Demo.IMAGE_KESERIM;
    private static final String AVATAR_TYPE_BRIDGE = "Eidolon Shadow";
    public static boolean ON;
    private static Unit avatarTrue;
    private static Unit avatarShade;

    private static int d;


    public static void togglePale() {
        if (ON) {
            Eidolons.onThisOrNonGdxThread(PaleAspect::exitPale);
        } else {
            Eidolons.onThisOrNonGdxThread(PaleAspect::enterPale);
        }
    }

    public static void exitPale() {
        ON = false;
        GuiEventManager.trigger(GuiEventType.POST_PROCESSING_RESET);
        important("Pale exited!");
        Eidolons.resetMainHero();
    }

    public static void enterPale() {
        d = 0;
        GuiEventManager.trigger(GuiEventType.POST_PROCESSING, PostFxUpdater.POST_FX_TEMPLATE.PALE_ASPECT);
        getAvatar();
            for (ChainHero hero : EidolonLord.lord.getChain().getHeroes()) {
                shadowLeapToLocation(hero.getUnit(), true);
            }
        shadowLeapToLocation(getAvatar(), false);
        Eidolons.bufferMainHero();
        Eidolons.setMainHero(getAvatar());

        ON = true;
        important("Pale eneterd!");
    }

    private static Unit createPaleAvatar(boolean alt) {
        Unit avatar = (Unit) DC_Game.game.createObject(DataManager.getType(alt ? AVATAR_TYPE_BRIDGE : AVATAR_TYPE,
                alt ? DC_TYPE.UNITS : DC_TYPE.CHARS),
                Eidolons.getMainHero().getX(),
                Eidolons.getMainHero().getY(), DC_Game.game.getPlayer(true));
        GuiEventManager.trigger(GuiEventType.UNIT_CREATED, avatar);
        avatar.setPale(true);
        avatar.getGame().getPaleMaster().objAdded(avatar);
        avatar.getGame().getObjMaster().remove(avatar, true);
        return avatar;
    }

    private static Unit getAvatar() {
        return getAvatar(EidolonsGame.BRIDGE);
    }

    public static Unit getAvatar(boolean shade) {
        Unit activeAvatar;
        if (shade) activeAvatar = avatarShade;
        else activeAvatar = avatarTrue;

        if (activeAvatar == null) {
            //reset vision etc
            if (shade) {
                avatarShade = createPaleAvatar(shade);
            } else {
                avatarTrue = createPaleAvatar(shade);
                for (ChainHero hero : EidolonLord.lord.getChain().getHeroes()) {
                    if (!hero.getUnit().isLeader()) {
                        GuiEventManager.trigger(GuiEventType.UNIT_CREATED, hero.getUnit());
                    }
                }
            }

        }
        return activeAvatar;
    }

    public static void shadowLeapToLocation(Coordinates coordinates) {
        shadowLeapToLocation(getAvatar(), false, coordinates);
    }

    public static void shadowLeapToLocation(Unit unit, boolean adjacent) {
        Coordinates c = Eidolons.getPlayerCoordinates();
        shadowLeapToLocation(unit, adjacent, c);
    }

    public static void shadowLeapToLocation(Unit unit, boolean adjacent, Coordinates c) {
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
            log(1, "PALE: failed to init LEAP" + unit);
            unit.setCoordinates(c);
            return;
        }

        unit.getGame().getLoop().activateAction(new ActionInput(unit.getSpell("Leap into Darkness"),
                unit.getGame().getCellByCoordinate(c)));
//        WaitMaster.receiveInput(WaitMaster.WAIT_OPERATIONS.ACTION_INPUT, new ActionInput(unit.getSpell("Leap into Darkness"),
//                avatar.getGame().getCellByCoordinate(c)));
    }
}
