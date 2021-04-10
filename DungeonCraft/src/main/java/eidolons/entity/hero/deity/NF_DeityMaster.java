package eidolons.entity.hero.deity;

import eidolons.content.PROPS;
import eidolons.entity.Deity;
import eidolons.entity.obj.attach.DC_BuffObj;
import eidolons.entity.obj.unit.Unit;
import eidolons.game.EidolonsGame;
import eidolons.game.core.game.DC_Game;
import main.ability.effects.Effect;
import main.content.DC_TYPE;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.entity.Ref;
import main.entity.type.ObjType;
import main.system.auxiliary.ContainerUtils;
import main.system.auxiliary.RandomWizard;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static eidolons.entity.hero.deity.DeityData.deity_value.nemesis;
import static eidolons.entity.hero.deity.DeityData.deity_value.patron;

public class NF_DeityMaster {
    private   DC_Game game;
    private   Map<Unit, Float> mapFavor= new HashMap<>();
    private   Map<Unit, Float> mapWrath= new HashMap<>();
    private   Map<Unit, DC_BuffObj> activeWrath= new HashMap<>();
    private  Map<Unit, DC_BuffObj> activeFavor= new HashMap<>();
    private Map<String, Deity> deityMap= new HashMap<>();

    public NF_DeityMaster(DC_Game game) {
        this.game = game;
    }

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
        DeityData data = createData(unit);
        for (String name : ContainerUtils.openContainer(data.getValue(nemesis))) {
            Deity deity =         getOrCreateDeity(name);
         list.add(deity);
        }
        unit.setNemesis(list);
        list = new LinkedList<>();
        for (String name : ContainerUtils.openContainer(data.getValue(patron))) {
            Deity deity =         getOrCreateDeity(name);
            list.add(deity);
        }
        unit.setPatron(list);
    }

    private Deity getOrCreateDeity(String name) {
        Deity deity = deityMap.get(name);
        if (deity == null) {
            ObjType type= DataManager.getType(name, DC_TYPE.DEITIES);
            deity = new Deity(type, game, new Ref(game));
        }
        return deity;
    }

    private DeityData createData(Unit unit) {
        return new DeityData(unit.getProperty(G_PROPS.DEITY));
    }

    public void flipEffect(boolean player, boolean wrath) {
        Unit target = null;
        DC_BuffObj active = activeWrath.get(target);
        active.kill();
        List<Deity> deity = target.getNemesis();
        DeityEffect effect = createEffect(target, wrath);
        // DC_BuffObj buff = createEffect(effect, target);
    }

    private DeityEffect createEffect(Unit target, boolean wrath) {
        //TODO
        DeityEffect deityEffect = new DeityEffect();
        return deityEffect;
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
