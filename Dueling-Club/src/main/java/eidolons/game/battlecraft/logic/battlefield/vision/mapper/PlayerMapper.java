package eidolons.game.battlecraft.logic.battlefield.vision.mapper;

import eidolons.entity.obj.BattleFieldObject;
import eidolons.game.battlecraft.logic.battle.universal.DC_Player;
import main.data.XLinkedMap;
import org.apache.poi.ss.formula.functions.T;

import java.util.Map;

/**
 * Created by JustMe on 4/1/2018.
 */
public class PlayerMapper {
    Map<DC_Player, Map<BattleFieldObject, T>> map = new XLinkedMap<>();

    public PlayerMapper() {
    }

    public void reset() {

    }
    public void log() {
        for (DC_Player player : map.keySet()) {
            log(player);
        }
    }
    public void log(DC_Player player) {
        main.system.auxiliary.log.LogMaster.log(1,player + "'s "+toString());
        for (BattleFieldObject object : map.get(player).keySet()) {
            main.system.auxiliary.log.LogMaster.log(1,object + " has "
             + map.get(player).get(object));

        }
    }


    public void set(DC_Player source, BattleFieldObject object,
                    T outlineType) {
        getMap(source).put(object, outlineType);
    }

    public T get(DC_Player source, BattleFieldObject object) {
        return getMap(source).get(object);
    }

    private Map<BattleFieldObject, T> getMap(DC_Player source) {
        Map<BattleFieldObject, T> map = this.map.get(source);
        if (map == null) {
            map = new XLinkedMap<>();
            this.map.put(source, map);
        }
        return map;
    }
}

