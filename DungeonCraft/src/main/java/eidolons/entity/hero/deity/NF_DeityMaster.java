package eidolons.entity.hero.deity;

import eidolons.content.PROPS;
import eidolons.entity.Deity;
import eidolons.entity.obj.attach.DC_BuffObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.core.game.DC_Game;
import main.ability.effects.Effect;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.RandomWizard;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NF_DeityMaster {
    DC_Game game;
    Map<Unit, Float> mapFavor;
    Map<Unit, Float> mapWrath;
    Map<Unit, DC_BuffObj> activeWrath;
    Map<Unit, DC_BuffObj> activeFavor;

    public static class DeityEffect {
        Object arg;
        DEITY_EFFECT type;
        int value;
        Effect.SPECIAL_EFFECTS_CASE triggerOn;

    }

    public enum DEITY_EFFECT {
        modify_value,

    }

    public void initDeities(Unit unit) {
        List<Deity> list = new LinkedList<>();
        for (String name : ContainerUtils.openContainer(unit.getProperty(PROPS.NEMESIS))) {
            Deity deity =         getOrCreateDeity(name);
         list.add(deity);
        }
        unit.setNemesis(list);
        list = new LinkedList<>();
        for (String name : ContainerUtils.openContainer(unit.getProperty(PROPS.PATRON))) {
            Deity deity =         getOrCreateDeity(name);
            list.add(deity);
        }
        unit.setPatron(list);
    }

    public void flipEffect(boolean player, boolean wrath) {
        Unit target;
        DC_BuffObj active = activeWrath.get(target);
        active.kill();
        List<Deity> deity = target.getNemesis();
        DeityEffect effect = createEffect(target, wrath);
        DC_BuffObj buff = createEffect(effect, target);
    }

    public void newRound() {
        for (Unit unit : game.getUnits()) {
            Float favorCoef = mapFavor.get(unit);
            favorCoef = rollTide(favorCoef, false);

        }
    }

    private Float rollTide(Float prev, boolean wrath) {
        return RandomWizard.getRandomFloatBetween(0.1f, 1);
    }


}
